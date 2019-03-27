#-*- coding: utf-8 -*-
import numpy as np
import math
import matplotlib.pyplot as plt
import csv

def sigmoid(x):
    return 1 / (np.exp(-x) + 1)

class Logistic:
    def __init__(self, penalty: float=0):
        '''
        Args:
            penalty: coefficient of regresion term
        '''
        self.penalty = penalty
        self.theta = None
        self.features = None
        self.labels = None
        self.dimen = 2

    def train(self, features: np.array, labels: np.array) -> np.array:
        '''train given dataset
        Args:
            features: features of labels
            labels: labels of the sample, either 1 or 0
        Return(np.array): weight vector
        '''
        raise NotImplementedError()

    def calc_cost(self) -> float:
        '''calculate cost of one sample
        Return(float): cost
        '''
        cost1 = -sum(self.labels * np.log(self.predict(self.features, self.theta)))
        cost2 = -sum((1 - self.labels) * np.log(1 - self.predict(self.features, self.theta)))
        regular = self.penalty * (self.theta.T @ self.theta) / 2
        return (cost1 + cost2 + regular) / len(self.labels)

    def predict(self, features, theta) -> float:
        '''Predict result with features and calculated w_vec
        '''
        return sigmoid(features @ theta)

# batch grad descend
class Logistic_BGD(Logistic):
    def __init__(self, penalty: float=0, lr: float=0.5, threshold: float=1e-10):
        Logistic.__init__(self, penalty)
        self.lr = lr
        self.threshold = threshold

    def pretrain(self, features: np.array, labels: np.array):
        '''do prepare work for trainning
        Args:
            features: features of labels
            labels: labels of the sample, either 1 or 0
        '''
        assert len(features) > 0
        self.features = np.insert(features, 0, 1, axis=1)
        self.labels = np.copy(labels)
        self.dimen = len(features[0])
        self.theta = np.zeros(self.dimen + 1)
    
    def train(self, features: np.array, labels: np.array) -> np.array:
        '''train given dataset
        Args:
            features: features of labels
            labels: labels of the sample, either 1 or 0
        Return(np.array): weight vector
        '''
        self.pretrain(features, labels)
        prev_cost = math.inf
        curr_cost = math.inf
        cnt = 1
        while True:
            self.update_theta()
            curr_cost = self.calc_cost()
            if abs(prev_cost - curr_cost) < self.threshold:
                break
            print("\rLoop %d with cost %f decend %f" % (cnt, curr_cost, abs(prev_cost - curr_cost)), end='')
            prev_cost = curr_cost
            cnt += 1
        return self.theta
        
    def update_theta(self):
        preds = self.predict(self.features, self.theta)
        grad = (self.features.T @ (preds - self.labels) + self.penalty * self.theta) / len(self.labels)
        self.theta -= self.lr * grad
        return self.theta


# Newton method
class Logistic_NT(Logistic):
    def __init__(self, penalty: float=0, lr: float=0.5, threshold: float=1e-10):
        Logistic.__init__(self, penalty)
        self.threshold = threshold
        self.lr = lr

    def pretrain(self, features: np.array, labels: np.array):
        '''do prepare work for trainning
        Args:
            features: features of labels
            labels: labels of the sample, either 1 or 0
        '''
        assert len(features) > 0
        self.features = np.insert(features, 0, 1, axis=1)
        self.labels = np.copy(labels)
        self.dimen = len(features[0])
        self.theta = np.random.uniform(0, 1, self.dimen + 1)

    def train(self, features: np.array, labels: np.array) -> np.array:
        '''train given dataset
        Args:
            features: features of labels
            labels: labels of the sample, either 1 or 0
        Return(np.array): weight vector
        '''
        self.pretrain(features, labels)
        prev_cost = math.inf
        curr_cost = math.inf
        cnt = 1
        while True:
            self.update_theta()
            curr_cost = self.calc_cost()
            if abs(prev_cost - curr_cost) < self.threshold:
                break
            print("\rLoop %d with cost %f decend %f" % (cnt, curr_cost, abs(prev_cost - curr_cost)), end='')
            prev_cost = curr_cost
            cnt += 1
        return self.theta
        
    def update_theta(self):
        preds = self.predict(self.features, self.theta)
        mat = np.array([[0 if i != j else (preds[j] * (1 - preds[j]) + self.penalty) \
            for j in range(len(self.features))] for i in range(len(self.features))])
        hessain = (self.features.T @ mat @ self.features) / len(self.labels)
        grad = (self.features.T @ (preds - self.labels) + self.theta) / len(self.labels)
        delta = np.linalg.solve(hessain, grad)
        self.theta -= self.lr * delta
        return self.theta

class DataDistribute:
    def __init__(self):
        self.mean0f0 = 0
        self.mean0f1 = 0
        self.mean1f0 = 0
        self.mean1f1 = 0
        self.var0f0 = 0
        self.var0f1 = 0
        self.var1f0 = 0
        self.var1f1 = 0

# generate data for two division problem
def gen_dataset(dimen, size0, size1, indep, diffvar):
    mu0 = np.random.uniform(-4, 4, 2)
    mu1 = np.random.uniform(-4, 4, 2)
    # print("[+] mu0:")
    # print(mu0)
    # print("[+] mu1")
    # print(mu1)
    if indep:
        tmp = np.diag([1, 1])
    else:
        tmp = np.random.normal(size=(2, 2))
    sigma0 = tmp.T @ np.diag(np.abs(np.random.normal(scale=1, size=dimen))) @ tmp
    sigma1 = tmp.T @ np.diag(np.abs(np.random.normal(scale=1, size=dimen))) @ tmp
    if not diffvar:
        sigma1 = sigma0
    # print("[+] sigma0:")
    # print(sigma0)
    # print("[+] sigma1:")
    # print(sigma1)
    data0 = np.random.multivariate_normal(mu0, sigma0, size0)
    data1 = np.random.multivariate_normal(mu1, sigma1, size1)
    return np.vstack((data0, data1)), np.array([0 for i in range(size0)] + [1 for i in range(size1)])

def test(trainer, features, labels):
    # train
    theta = trainer.train(features, labels)
    
    size0 = labels.tolist().count(0)
    # show results
    figure, ax = plt.subplots()
    ro, gd = ax.plot(features[np.where(labels)][:, 0], features[np.where(labels)][:, 1], "ro", 
        features[np.where(np.logical_not(labels))][:, 0], features[np.where(np.logical_not(labels))][:, 1], "gd")
    def boundary(x0, theta):
        x1 = (-theta[0] - theta[1] * x0) / theta[2]
        return x1
    high = ax.axis()[1]
    low = ax.axis()[0]
    line = [[low, boundary(low, theta)], [high, boundary(high, theta)]]
    line_x, line_y = zip(*line)
    ax.add_line(plt.Line2D(line_x, line_y, color="black"))
    ax.set_xlabel("x1")
    ax.set_ylabel("x2")
    ax.legend([ro, gd], ["class0", "class1"], loc="upper right")
    figure.show()

def test_iris(trainer, filename):
    with open(filename, "r") as f:
        reader = csv.reader(f, delimiter=',')
        dataset = list(reader)
    dataset = np.array(dataset)
    labels = np.array(list(map(lambda x: x.startswith("Iris-s"), dataset[:,4].tolist())))
    features = np.vstack((dataset[:,2], dataset[:,3])).T
    features = features.astype(np.float)
    
    test(trainer, features, labels)


if __name__ == "__main__":
    # data generation
    # features, labels = gen_dataset(2, 30, 30, indep=False, diffvar=True)

    # trainer = Logistic_NT(penalty=0.1, threshold=1e-10)
    trainer = Logistic_BGD(penalty=0.1, threshold=1e-14)
    
    # test(trainer, features, labels)
    test_iris(trainer, "iris.data")
