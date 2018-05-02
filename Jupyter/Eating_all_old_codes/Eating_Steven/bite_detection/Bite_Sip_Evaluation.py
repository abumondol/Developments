
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
#import matplotlib.pyplot as plt
from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split


# In[2]:


from keras.layers import Conv2D, MaxPooling2D, Bidirectional, LSTM, Dense, Flatten, Dropout, Activation
from keras.models import Sequential, model_from_json, load_model


# In[3]:


path = 'C:/ASM/DevData/eating_steven' if 'C:' in os.getcwd() else '.'
with open(path + '/data/lab_data_steven.pkl', 'rb') as file:
    data = pickle.load(file)
    
with open(path + '/data/lab_windows_steven.pkl', 'rb') as file:
    windows = pickle.load(file)    


# In[5]:


def get_test_windows_labels(data, windows, subject, sess, vth):
    win_size=80    
    axis_count =9
    half_win_size = win_size//2
    
    print("Generating Test data ... subj, sess: ", subj, sess)
            
    d = data[subj][sess]["data_right"]
    mn, mx = -2*9.8, 2*9.8
    d[:, 1:4] = (np.clip(d[:, 1:4], mn, mx)-mn)/(mx-mn)

    mn, mx = -15, 15
    d[:, 4:7] = (np.clip(d[:, 4:7], mn, mx)-mn)/(mx-mn)

    indices = windows[subj][sess]["w_indices"]
    test_y = windows[subj][sess]["labels"][:, 0]
    var_mask = (indices[:, -1]>=vth)
    
    
    wcount = len(indices)        
    test_x = np.zeros((wcount, win_size, axis_count))        
    for i in range(wcount):
        j = int(indices[i, 0])
        test_x[i, :, :] = d[j-half_win_size:j+half_win_size, 1:]


    assert len(test_x) ==len(test_y)
    test_y = test_y.reshape((-1, 1))
    
    return test_x, test_y, indices[:, 0], var_mask


# In[ ]:


def get_bites(proba_y, indices, var_mask):
    


# In[ ]:


def evaluate_bite_detection(gt_bite, pr_bite):
    


# In[11]:


for subj in range(7):
    if subj!=0: continue
    model = load_model(path+'/models/right_hand_lab/keras_blstm_lab_'+str(subj)+'.h5')
        
    for sess in range(len(data[subj])):
        if sess!=0: continue
        
        test_x, test_y, indices, var_mask = get_test_windows_labels(data, windows, subj, sess, vth=0.5)
        print("Shapes: ", test_x.shape, test_y.shape, indices.shape, var_mask.shape)        
        
        proba_y = model.predict(test_x)
        
        
        print(np.sum(proba_y>=0.5))    
        

