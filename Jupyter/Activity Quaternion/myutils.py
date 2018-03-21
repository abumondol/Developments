
# coding: utf-8

# In[1]:

import numpy as np


# In[2]:

def change_labels(data_y):
    data_y[data_y == 406516] = 1
    data_y[data_y == 406517] = 2
    data_y[data_y == 404516] = 3
    data_y[data_y == 404517] = 4
    data_y[data_y == 406520] = 5
    data_y[data_y == 404520] = 6
    data_y[data_y == 406505] = 7
    data_y[data_y == 404505] = 8
    data_y[data_y == 406519] = 9
    data_y[data_y == 404519] = 10
    data_y[data_y == 406511] = 11
    data_y[data_y == 404511] = 12
    data_y[data_y == 406508] = 13
    data_y[data_y == 404508] = 14
    data_y[data_y == 408512] = 15
    data_y[data_y == 407521] = 16
    data_y[data_y == 405506] = 17
    return data_y

def get_train_test_data(data):
    train_data, train_label = np.empty((0, 18)), []
    test_data, test_label = np.empty((0, 18)), []
    for s in range(4):
        for sess in range(6):
            if s>0 and sess>=4:
                test_data = np.concatenate((test_data, data[s][sess]['right']))
                test_label = np.concatenate((test_label, data[s][sess]['labels'][:, -1]))
            else:
                train_data = np.concatenate((train_data, data[s][sess]['right']))
                train_label = np.concatenate((train_label, data[s][sess]['labels'][:, -1]))

    train_label = change_labels(train_label)
    test_label = change_labels(test_label)

    train_label = train_label.astype(int)
    test_label = test_label.astype(int)
    
    return train_data, train_label, test_data, test_label


# In[ ]:

def znorm(train, test):
    m = np.mean(train, axis=0)
    s = np.std(train, axis=0)
    print('Means: ', m)
    print('Mtds: ', s)
    train = (train-m)/s
    test = (test-m)/s
    return train, test

def minmax_norm(train, test):
    mn = np.amin(train, axis=0)
    mx = np.amax(train, axis=0)
    print('Mins: ', mn)
    print('Maxs: ', mx)
    train = (train-mn)/(mx-mn)
    test = (test-mn)/(mx-mn)
    return train, test

def magnorm(d):
    mag = np.sqrt(np.sum(d*d, axis=1))
    mag = mag.reshape((-1, 1))
    mag = np.concatenate((mag, mag, mag), axis=1)
    d = d/mag
    return d


# In[ ]:

def get_windows(x, y, win_len, step_size, augment_vals=[]):    
    num_classes=18
    r, c = x.shape
    ix = np.arange(0, r-win_len, step_size)
    count = len(ix)
    resX, resY = np.empty((count, win_len, c)), np.empty((count, num_classes), dtype=int)
        
    for i in range(count):
        s = ix[i]        
        resX[i, :, :] = x[s:s+win_len, :]
        label = y[s+win_len-1]
        resY[i, label] = 1
    
    resX = resX.reshape((count, win_len, c, 1))
    return resX, resY    

