import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from skimage.io import imread, imsave
from skimage import transform
import struct

def pca(X: np.array, n_components: int):
    mean = np.mean(X, axis=0)
    X = X - mean
    cov = X.T @ X
    # calculate eigen values and eigen vector
    eigvals, eigvecs = np.linalg.eig(cov)
    
    # get components, from max to min
    order = np.argsort(eigvals)[::-1]
    components = eigvecs[:, order[:n_components]]

    # transform
    Z = X @ components
    Z = Z.astype(np.float)
    Y = Z @ components.T + mean
    Y = Y.astype(np.float)
    
    return Z, Y

def gen_test_data(size, mean, cov):
    return np.random.multivariate_normal(mean, cov, size=size).T

def test_pca():
    mean = np.array([11, 21, 31])
    cov = np.array([[3, 6, 4], [1, 1, 1], [4, 6, 3]])
    x, y, z = gen_test_data(100, mean, cov)
    X = np.vstack((x, y, z)).T
    _, new_X = pca(X, 2)
    new_x, new_y, new_z = new_X[:, 0], new_X[:, 1], new_X[:, 2]

    figure = plt.figure()
    ax = figure.add_subplot(111, projection='3d')
    ax.scatter(x, y ,z, c='g')
    ax.scatter(new_x, new_y, new_z, c="r")
    ax.set_xlabel('X')
    ax.set_ylabel('Y')
    ax.set_zlabel('Z')
    ax.legend(["origin", "new"])
    figure.show()

def test_pca_minist(n, n_components=2, show_result=True, store_img=False):
    global rows, cols
    colors = ["black", "red", "cyan", "orange", "yellow", "green", "blue", "violet", "deeppink", "grey"]
    imgs, labels = read_images(n)
    X, new_imgs= pca(imgs, n_components)

    # calculate snr
    snr = calc_snr(imgs, new_imgs)
    avg_snr = np.mean(snr)
    print("[+] Average SNR: {:.2f}".format(avg_snr))

    # plot result
    if show_result and n_components == 2:
        figure = plt.figure()
        ax = figure.add_subplot(111)
        for i in range(10):
            subX = X[np.where(labels==i)]
            ax.scatter(subX[:, 0], subX[:, 1], c=colors[i])
        ax.legend(range(10))
        figure.show()
    elif show_result and n_components == 3:
        figure = plt.figure()
        ax = figure.add_subplot(111, projection='3d')
        for i in range(10):
            subX = X[np.where(labels==i)]
            ax.scatter(subX[:, 0], subX[:, 1], subX[:, 2], c=colors[i])
        ax.legend(range(10))
        figure.show()

    if store_img:
        # store images
        print("[.] Storing images")
        for i in range(min(new_imgs.shape[0], 10)):
            imsave("newpic/pic%04d.png" % i, np.round(new_imgs[i].reshape(rows, cols)).astype(np.uint8))
        print("[+] Finished")
    
    return snr

def calc_snr(data1, data2):
    assert(len(data1) == len(data2))
    diff = np.abs(data1 - data2)
    snr = 10 * (np.log10(np.sum(data1 ** 2, axis=1)) - np.log10(np.sum(diff ** 2, axis=1)))
    return np.abs(snr)

def read_images(n):
    global rows, cols
    imgs = []
    with open("./t10k-images.idx3-ubyte", "rb") as f:
        _ = u32(f.read(4)) # magic_number
        img_n = u32(f.read(4))
        rows = u32(f.read(4))
        cols = u32(f.read(4))
        n = min(n, img_n)
        for _ in range(n):
            raw_img = f.read(rows * cols)
            img = np.array(list(raw_img))
            imgs.append(img)
        imgs = np.array(imgs)
    with open("./t10k-labels.idx1-ubyte", "rb") as f:
        u32(f.read(4)) # magic_number
        u32(f.read(4)) # items number
        labels = np.array(list(f.read(n)))
    return imgs, labels

def u32(data):
    return struct.unpack(">I", data)[0]

def test_fun(n, n_components):
    global rows, cols
    imgs, _ = read_images(n)
    X = imgs
    mean = np.mean(X, axis=0)
    X = X - mean
    cov = X.T @ X
    # calculate eigen values and eigen vector
    eigvals, eigvecs = np.linalg.eig(cov)
    
    # get components, from max to min
    order = np.argsort(eigvals)[::-1]
    components = eigvecs[:, order[:n_components]]
    plt.imshow(components[:,1].reshape(rows, cols), cmap='gray')
    plt.show()


if __name__ == "__main__":
    # test pca with self-generated data set
    # test_pca()

    # test pca with mnist dataset
    # test_pca_minist(10000, n_components=256, show_result=False, store_img=True)
    test_fun(10000, 16)
