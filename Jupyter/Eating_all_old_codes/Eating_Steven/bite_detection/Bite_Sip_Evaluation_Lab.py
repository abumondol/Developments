
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt


# In[2]:


from keras.layers import Conv2D, MaxPooling2D, Bidirectional, LSTM, Dense, Flatten, Dropout, Activation
from keras.models import Sequential, model_from_json, load_model


# In[3]:


path = 'C:/ASM/DevData/eating_steven' if 'C:' in os.getcwd() else '.'

with open(path + '/data/lab_data_steven.pkl', 'rb') as file:
    data = pickle.load(file)
    
with open(path + '/data/lab_windows_steven.pkl', 'rb') as file:
    w_indices = pickle.load(file)    


# In[4]:


def get_test_windows_labels(data, w_indices, subj, sess, vth):
    win_size=80    
    axis_count =9
    half_win_size = win_size//2
    
    print("Generating Test data ... subj, sess: ", subj, sess)
            
    d = data[subj][sess]["data_right"]
    mn, mx = -2*9.8, 2*9.8
    d[:, 1:4] = (np.clip(d[:, 1:4], mn, mx)-mn)/(mx-mn)

    mn, mx = -15, 15
    d[:, 4:7] = (np.clip(d[:, 4:7], mn, mx)-mn)/(mx-mn)

    indices = w_indices[subj][sess]["w_indices"]
    test_y = w_indices[subj][sess]["labels"][:, 0]
    
    var_mask = (indices[:, -1]>=vth)
    indices = indices[var_mask, :]
    test_y = test_y[var_mask]
    
    wcount = len(indices)        
    test_x = np.zeros((wcount, win_size, axis_count))        
    for i in range(wcount):
        j = int(indices[i, 0])
        test_x[i, :, :] = d[j-half_win_size:j+half_win_size, 1:]


    assert len(test_x) ==len(test_y)
    #test_y = test_y.reshape((-1, 1))
    
    return test_x, test_y, indices


# In[5]:


def get_bites(proba_y, indices, var_mask, proba_th=0.5):
    pass
    


# In[6]:


def evaluate_bite_detection(gt_bite, pr_bite, offset=0):
    pass


# In[7]:


subj, sess = 0, 0    
model = load_model(path+'/models/right_hand_lab/keras_blstm_lab_'+str(subj)+'.h5')

print("Original shape: ", w_indices[subj][sess]["w_indices"].shape)
test_x, test_y, indices = get_test_windows_labels(data, windows, subj, sess, vth=0.5)
print("Shapes: ", test_x.shape, test_y.shape, indices.shape)        

proba_y = model.predict(test_x)
print("Proba done... positive: ", np.sum(proba_y>=0.5))


# In[ ]:


a = data[subj][sess]["annots"]
print(len(a), np.sum(a[:,2]==1), np.sum(a[:, 2]==2))
step = 16*60*20
#print(a[:,0]/16)
for i in range(0, len(data[subj][sess]["data_right"]), step):
    cond = (i<=indices[:, 0]) & (indices[:, 0]<=i+step)
    
    if np.sum(cond)==0: 
        continue

    p = proba_y[cond]
    v = indices[cond, -1]
    t = indices[cond, 0]/16

    cond = (i<=a[:,0]) & (a[:,0]<=i+step)
    a1 = a[cond, 0]/16
    cond = (i<=a[:,1]) & (a[:,1]<=i+step)
    a2 = a[cond, 1]/16

    fig, ax = plt.subplots(figsize=(16,6))
    ax.scatter(t, p, marker='.', color='green')    
    ax.scatter(a1, np.zeros((len(a1), )), marker='o', s=100, color='red')
    #a2 = a2[a2!=a1]
    ax.scatter(a2, np.zeros((len(a2), )), marker='x', s =100, color='blue')
    plt.title("Time: "+str(i/16)+" - "+str((i+step)/16))
    plt.show()

    fig, ax = plt.subplots(figsize=(16,6))
    ax.scatter(t, v, marker='.')
    ax.scatter(a1, np.zeros((len(a1), )), marker='o')
    ax.scatter(a2, np.zeros((len(a2), )), marker='x')
    plt.show()

