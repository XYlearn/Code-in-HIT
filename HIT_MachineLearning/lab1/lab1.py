#!/bin/python3
# -*- coding: utf-8 -*-
import numpy as np
import datetime
import matplotlib.pyplot as plt
import math

class DataSetGenerator:
    """data set generator"""
    def __init__(self, low: float=-1.0, high: float=1.0, loc: float=0.0, scale: float=1e-3, function=lambda x: np.sin(x)):
        '''
        Args:
            low: lower bound of variable x
            high: higher bound of variable x
            loc: mean value of normal distribution of noise
            scale: variant of normal distribution of noise
            function: callable object taking a varaible x then return a value y
        '''
        self.low = low
        self.high = high
        self.loc = loc
        self.scale = scale
        self.function = function

    def gen_data_set(self, n: int, pathname: str=None):
        '''generate data set of size n
        Args:
            n: size of data set to generate
        '''
        x_vec = np.random.uniform(self.low, self.high, n)
        y_vec = self.function(x_vec) + np.random.normal(self.loc, self.scale)
        data_set = np.vstack((x_vec, y_vec)).T
        if pathname:
            np.savetxt(pathname, data_set)
        return data_set


class DataSetManager:
    def __init__(self, data_set):
        self.data_set = data_set

    @staticmethod
    def load(pathname):
        data_set  = np.loadtxt(pathname)
        manager = DataSetManager(data_set)
        return manager

    @staticmethod
    def get_xy_vec(data_set):
        tmp = data_set.T
        return tmp[0], tmp[1]


class Fitter:
    def __init__(self, penalty: float=0, order: int=9):
        '''
        Args:
            penalty: coefficient of regresion term
            order: max order of Taylor series expansion
        '''
        self.penalty = penalty
        self.order = order

    def calc_loss(self, x_vec: np.array, y_vec: np.array, w_vec: np.array) -> float:
        '''
        Args:
            x_vec: vector of x variables
            y_vec: vector of y variables
            w_vec: vector of parameters of Tylor series expansion
        Return(float): loss of parameter w under data set (x, y)
        '''
        x_matrix = build_x_mat(x_vec, self.order)
        tmp = x_matrix @ w_vec - y_vec
        loss = (tmp.T @ tmp) / 2
        loss += np.sum(w_vec * w_vec) * self.penalty / 2
        # loss = np.sqrt(loss * 2 / x_vec.size)
        return loss

    def calc_w(self, x_vec: np.array, y_vec: np.array):
        raise NotImplementedError("Please overwrite this method")


class LinearFitter(Fitter):
    """Fitter using analytical solution"""
    def __init__(self, penalty: float=0, order: int=9):
        Fitter.__init__(self, penalty, order)

    def calc_w(self, x_vec: np.array, y_vec: np.array) -> np.array:
        x_matrix = build_x_mat(x_vec, self.order)
        t_vector = y_vec.T
        tmp = (x_matrix.T @ x_matrix + np.eye(self.order + 1) * self.penalty)
        tmp = np.linalg.inv(tmp)
        w_vec = tmp @ ((x_matrix.T) @ t_vector)
        return w_vec


class BatchGradDescFitter(Fitter):
    """Fitter using batch gradient descent method"""
    def __init__(self, penalty: float=0, order: int=9, lr: float=math.exp(-3), eps: float=math.exp(-8)):
        '''
        Args:
            penalty: coefficient of regresion term
            order: max order of Taylor series expansion
            lr: learning rate
            eps: the destination loss value
        '''
        Fitter.__init__(self, penalty, order)
        self.lr = lr
        self.eps = eps

    def calc_w(self, x_vec: np.array, y_vec: np.array) -> np.array:
        w_vec = self.gen_init_w()
        # normalization
        reg_num = (np.max(np.abs(x_vec)))
        reg_x_vec = x_vec / reg_num
        x_matrix = build_x_mat(reg_x_vec, self.order)
        loss = 0
        cnt = 1
        while True:
            w_vec -= self.lr * (x_matrix.T @ (x_matrix @ w_vec - y_vec) + self.penalty * w_vec) / y_vec.size
            new_loss = self.calc_loss(reg_x_vec, y_vec, w_vec / np.fromfunction(lambda i: reg_num ** i, (w_vec.size, )))
            diff = abs(new_loss - loss)
            if diff < self.eps:
                break
            print("\r[.] calculating: %d loops. diff %f(%f)" % (cnt, diff, self.eps), end='')
            cnt += 1
            loss = new_loss
        print("\n")
        w_vec = w_vec / np.fromfunction(lambda i: reg_num ** i, (w_vec.size, ))
        return w_vec

    def gen_init_w(self, low=-1.0, high=1.0):
        # stochastic gradient descent
        return np.random.uniform(low, high, self.order + 1).T


class ConjGradFitter(Fitter):
    """Fitter using conjugate gradient method"""
    def __init__(self, penalty=0, order=9, eps=math.exp(-8)):
        '''
        Args:
            penalty: coefficient of regresion term
            order: max order of Taylor series expansion
            eps: the destination loss value
        '''
        self.penalty = penalty
        self.order = order
        self.eps = eps

    def calc_w(self, x_vec: np.array, y_vec: np.array) -> np.array:
        x_mat = build_x_mat(x_vec, order=self.order)
        q_mat = x_mat.T @ x_mat + self.penalty * np.eye(self.order + 1)
        # w_vec = self.gen_init_w() # to calculate
        w_vec = np.zeros(self.order + 1)
        r_vec = (x_mat.T @ y_vec) - (q_mat @ w_vec) # one of linear independant vector
        p_vec = r_vec # one of conjugate vectors
        loss = 0
        cnt = 0
        while True:
            # calculate learning rate
            lr = (r_vec.T @ r_vec) / (p_vec.T @ q_mat @ p_vec)
            w_vec += lr * p_vec
            prev_r_vec = r_vec
            r_vec -= lr * (q_mat @ p_vec)
            new_loss = self.calc_loss(x_vec, y_vec, w_vec)
            diff = abs(new_loss - loss)
            loss = new_loss
            if diff < self.eps:
                break
            # Gram－Schmidt method to calculate next base vector
            p_vec = r_vec + (r_vec.T @ r_vec / (prev_r_vec.T @ prev_r_vec)) * p_vec
            print("\r[.] calculating: %d loops. diff %f(%f)" % (cnt, diff, self.eps), end='')
            cnt += 1
        print('')
        return w_vec


    def gen_init_w(self, low=-1.0, high=1.0):
        # stochastic gradient descent
        return np.random.uniform(low, high, self.order + 1).T


class Plotter:
    def __init__(self, low=-1.0, high=1.0):
        self.low = low
        self.high = high

    def visualize_res(self, w_vec):
        x = np.linspace(self.low, self.high, 100)
        y = build_x_mat(x, w_vec.size - 1) @ w_vec
        plt.plot(x, y, 'y')

    def visualize_orig(self, x_vec, y_vec):
        plt.plot(x_vec, y_vec, "ro")

    def show(self):
        plt.show()


def build_x_mat(x_vec, order=6):
    return np.fromfunction(lambda i, j: x_vec[i] ** j, (x_vec.size, order + 1), dtype=int)


def test(fitter, plotter=None):
    print("Testing %s" % fitter.__class__.__name__)
    # generate data set
    generator = DataSetGenerator(scale=1e-1, function=lambda x: np.sin(2 * x * np.pi))
    data_set = generator.gen_data_set(100)
    x_vec, y_vec  = DataSetManager.get_xy_vec(data_set)

    # calculate parameters
    start_time = datetime.datetime.now()
    w_vec = fitter.calc_w(x_vec, y_vec)
    end_time = datetime.datetime.now()
    print("[+] time comsuming: %d seconds" % (end_time - start_time).microseconds)

    loss = fitter.calc_loss(x_vec, y_vec, w_vec)
    print("[+] loss:", loss)

    # visualize resulte
    if not plotter:
        plotter = Plotter()
    plotter.visualize_res(w_vec)
    plotter.visualize_orig(x_vec, y_vec)
    plotter.show()


def temp_drawer():
    generator = DataSetGenerator(scale=1e-1, function=lambda x: np.sin(2 * x * np.pi))
    data_set = generator.gen_data_set(100)
    x_vec, y_vec  = DataSetManager.get_xy_vec(data_set)
    plt.plot(x_vec, y_vec, "ro")

    fitter = LinearFitter(penalty=0, order=9)
    fitter.calc_w(x_vec, y_vec)
    plt.plot()

if __name__ == "__main__":
    # fitter = LinearFitter(penalty=0, order=40)
    # fitter = BatchGradDescFitter(penalty=0, order=9, lr=0.9, eps=math.exp(-12))
    fitter = ConjGradFitter(penalty=1e-6, order=10, eps=math.exp(-12))
    test(fitter)
