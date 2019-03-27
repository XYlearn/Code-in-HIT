import numpy as np
import matplotlib.pyplot as plt
import math
import re
import itertools

def euc_distance(a, b):
    return np.sqrt(np.sum((a-b)**2))

class KMeans:
    def __init__(self, distance: callable, clznum: int=2):
        """
        Args:
            distance: method receive 2 arguments and return distance between them
            clznum: numer of classes to cluster
        """
        self.distance = distance
        self.clznum = clznum
        self.dimen = 0
        self.dataset = None

    def update_dataset(self, dataset):
        self.dataset = dataset.copy()
        self.dimen = dataset.shape[1]

    def init_k(self):
        dtset = self.dataset.copy()
        remain = self.clznum - 1
        dists = []
        kp = [dtset[0]]
        # the farther the better
        while remain:
            dist = [self.distance(kp[-1], dtset[i]) for i in range(len(dtset))]
            dists.append(dist)
            i = max(range(len(dtset)), key=lambda i: sum([dists[j][i] for j in range(len(dists))]))
            kp.append(dtset[i])
            remain -= 1
        return kp

    def cluster(self):
        kp = self.init_k()
        # iteration until kp don't change
        while True:
            not_changed = True
            clz = [[] for i in range(self.clznum)]
            # decide which class a point belong to
            for p in self.dataset:
                idx = min(range(self.clznum), key=lambda i: self.distance(kp[i], p))
                clz[idx].append(p)
            # update kp
            # here I use the mean value of every point, also we can use
            # Minkowski, Euclidean or CityBlock distance
            for i in range(self.clznum):
                new_center = np.mean(np.array(clz[i]), axis=0)
                if np.count_nonzero(kp[i] == new_center) != self.dimen:
                    kp[i] = new_center
                    not_changed = False
            if not_changed:
                break
        return np.array(kp)


def gaussain(x, mu, sigma):
    coef = 1.0 / np.linalg.det(sigma)
    diff = (x - mu)
    res = coef * np.exp(-0.5 * diff @ np.linalg.inv(sigma) @ diff.T)
    return res


class EM:
    """Expectation Maximize algorithm for clustering gaussain distribution
    """
    def __init__(self, threshold: float=1e-4, clznum: int=2):
        self.threshold = threshold
        self.clznum = clznum
        self.dimen = 0
        self.max_iter = 500
        self.dataset = None
        self.mu = None
        self.sigma = None
        self.pi = None
        self.posterior = None
        self.kmeans = None

    def update_dataset(self, dataset: np.array):
        self.dataset = dataset.copy()
        self.kmeans = KMeans(euc_distance, self.clznum)

        self.dimen = dataset.shape[1]
        self.kmeans.update_dataset(dataset)
        self.mu = self.kmeans.cluster()
        self.sigma = np.array([np.diag(np.ones(self.dimen)) for i in range(self.clznum)])
        self.pi = np.random.dirichlet(np.ones(self.clznum), 1)[0]
        self.posterior = np.empty((len(self.dataset), self.clznum))
        

    def cluster(self):
        for _ in range(self.max_iter):
            self.e_step()
            old_mu = self.m_step()
            change = np.sum(np.abs(self.mu - old_mu))
            print("\rchange {}".format(change), end='')
            if change < self.threshold:
                break
        return self.pi, self.mu, self.sigma

    def e_step(self):
        '''calculate posterior'''
        for i in range(len(self.dataset)):
            sumup = 0
            for j in range(self.clznum):
                sumup += self.pi[j] * gaussain(self.dataset[i], self.mu[j], self.sigma[j])
            for j in range(self.clznum):
                self.posterior[i][j] = self.pi[j] * gaussain(self.dataset[i], self.mu[j], self.sigma[j]) / sumup
        # print("posterior")
        # print(self.posterior)

    def m_step(self):
        '''calculate mu, sigma and pi with new posterior'''
        self.update_pi()
        old_mu = self.update_mu()
        self.update_sigma()
        return old_mu

    def update_pi(self):
        '''update pi in m step'''
        for k in range(self.clznum):
            self.pi[k] = np.sum(self.posterior[:, k]) / len(self.dataset)
        # print("pi")
        # print(self.pi)

    def update_mu(self):
        '''update mu in m step'''
        old_mu = self.mu.copy()
        for k in range(self.clznum):
            mu_k = np.zeros(self.dimen)
            for i in range(len(self.dataset)):
                mu_k += self.posterior[i][k] * self.dataset[i]
            self.mu[k] = mu_k / np.sum(self.posterior[:, k])
        return old_mu
        # print("mu")
        # print(self.mu)

    def update_sigma(self):
        '''update sigma in m step'''
        for k in range(self.clznum):
            sigma_k = np.diag(np.zeros(self.dimen))
            for i in range(len(self.dataset)):
                tmp = (self.dataset[i] - self.mu[k]).reshape(self.dimen, 1)
                sigma_k += self.posterior[i][k] * (tmp @ tmp.T)
            self.sigma[k] = sigma_k / np.sum(self.posterior[:, k])
        # print("sigma")
        # print(self.sigma)
        # print('\n')


def mixture_gaussian(clznum, size):
    pi = np.random.dirichlet(np.ones(clznum), size=1)[0] * (size / 2) + (size / clznum / 2)
    data = []
    for i in range(clznum):
        mu = np.random.normal(0, 3, 2)
        tmp = np.diag(np.random.normal(2, 1, 2)) + np.random.normal(0, 0.1, (2, 2))
        cov = tmp.T @ np.abs(np.diag(np.random.normal(0, 2, 2))) @ tmp
        data.append(np.random.multivariate_normal(mu, cov, int(round(pi[i]))).T)
        print("mu[{}]".format(i))
        print(mu)
        print("sigma[{}]".format(i))
        print(cov)
    return np.concatenate(data, axis=1).T

def gen_dataset(clznum, size):
    means = [np.random.normal(0, 5, 2) for i in range(clznum)]
    print(means)
    d0 = np.array(np.concatenate([np.random.normal(mean[0], 1, size) for mean in means]))
    d1 = np.array(np.concatenate([np.random.normal(mean[1], 1, size) for mean in means]))
    return np.vstack((d0, d1)).T

def get_circle(centroid, ccov):
    sdwidth = 1.7
    points = 100
    mean = np.c_[centroid] 
    tt = np.c_[np.linspace(0, 2 * np.pi, points)]
    x = np.cos(tt)
    y = np.sin(tt)
    ap = np.concatenate((x, y), axis=1).T
    d, v = np.linalg.eig(ccov)
    d[np.where(d < 0)] = 0
    d = np.diag(d)
    d = sdwidth * np.sqrt(d)
    bp = np.dot(v, np.dot(d, ap)) + np.tile(mean, (1, ap.shape[1]))
    return bp[0, :], bp[1, :]

def test_mixture_gaussian():
    data = mixture_gaussian(3, 80)
    plt.plot(data[0], data[1], "go")
    plt.show()

def test_kmean(dtset, clznum=3):
    kmeans = KMeans(euc_distance, clznum)
    kmeans.update_dataset(dtset)
    kp = kmeans.cluster()
    print(kp)
    # visualization
    plt.plot(dtset[:,0], dtset[:,1], "go")
    plt.plot(kp[:,0], kp[:,1], "ro")
    plt.show()

def test_em(dataset, clznum=3):
    em = EM(clznum = clznum)
    em.update_dataset(dataset)
    pi, mu, sigma = em.cluster()
    print("[pi]")
    print(pi)
    print("[mu]")
    print(mu)
    print("[sigma]")
    print(sigma)
    # visualization
    for k in range(clznum):
        x, y = get_circle(mu[k], sigma[k])
        plt.plot(x, y)
    plt.plot(dataset.T[0], dataset.T[1], "go")
    plt.show()

def get_uci_data(filename):
    with open(filename, "r") as f:
        cont = f.read()
    lines = filter(lambda s: len(s.strip()), cont.split("\n"))
    datas = list(map(lambda line: np.array(line.split('\t')[:-1]), lines))
    datas = np.array(datas).astype(np.float)
    return datas

def get_uci_tags(filename):
    with open(filename, "r") as f:
        cont = f.read()
    lines = filter(lambda s: len(s.strip()), cont.split("\n"))
    tagtype = ["very_low", "Low", "Middle", "High"]
    tagss = map(lambda line: line.split('\t')[-1].strip(), lines)
    tags = np.array(list(map(lambda tag: tagtype.index(tag), tagss)))
    return tags

def verify_kmean(dataset, tags, clznum):
    kmeans = KMeans(euc_distance, clznum)
    kmeans.update_dataset(dataset)
    kp = kmeans.cluster()
    print(kp)
    real_clzp = np.array([dataset[np.where(tags==i)] for i in range(clznum)])
    clzp = [[] for _ in range(clznum)]
    for i in range(len(dataset)):
        clz = 0
        low_dist = 1e20
        for k in range(clznum):
            dist = euc_distance(kp[k], dataset[i])
            if dist < low_dist:
                clz = k
                low_dist = dist
        clzp[clz].append(dataset[i])
    clzp = np.array(list(map(lambda x: np.array(x), clzp)))
    print("correct rate: {}".format(get_correctness(clzp, real_clzp, clznum) / len(dataset)))

def verify_em(dataset, tags, clznum):
    em = EM(clznum = clznum)
    em.update_dataset(dataset)
    pi, mu, sigma = em.cluster()
    print("[pi]")
    print(pi)
    print("[mu]")
    print(mu)
    print("[sigma]")
    print(sigma)
    real_clzp = np.array([dataset[np.where(tags==i)] for i in range(clznum)])
    clzp = [[] for _ in range(clznum)]
    for i in range(len(dataset)):
        clz = 0
        low_posterior = 1e20
        for k in range(clznum):
            posterior = em.posterior[i][k]
            if posterior < low_posterior:
                clz = k
                low_posterior = posterior
        clzp[clz].append(dataset[i])
    clzp = np.array(list(map(lambda x: np.array(x), clzp)))
    print("correct rate: {}".format(get_correctness(clzp, real_clzp, clznum) / len(dataset)))

def get_correctness(clzp, real_clzp, clznum):
    intersect_number = [[0 for _ in range(clznum)] for _ in range(clznum)]
    for i in range(clznum):
        for j in range(clznum):
            _, ncols = clzp[i].shape
            dtype = {
                'names': ['f{}'.format(k) for k in range(ncols)],
                'formats': ncols * [clzp[i].dtype]
            }
            intersect_number[i][j] = len(np.intersect1d(clzp[i].view(dtype), 
                                        real_clzp[j].view(dtype)))
    # print(intersect_number)
    max_correct = 0
    max_per = None
    for per in itertools.permutations(range(clznum)):
        correct = 0
        for i, j in enumerate(per):
            correct += intersect_number[i][j]
        if correct > max_correct:
            max_correct = correct
            max_per = per
    return max_correct

if __name__ == "__main__":
    # dataset = gen_datasets(3, 30)
    # test kmean with classified gaussin
    # test_kmean(dataset)

    # dataset = mixture_gaussian(3, 90)
    # test kmean with mixture gaussin
    # test_kmean(dataset, 3)

    # test em with mixture gaussin
    # test_em(dataset, 3)

    dataset = get_uci_data("./uci_dataset")
    tags = get_uci_tags("./uci_dataset")
    # test kmean with uci data
    # verify_kmean(dataset, tags, 4)

    # test em with uci data
    verify_em(dataset, tags, 4)
    
