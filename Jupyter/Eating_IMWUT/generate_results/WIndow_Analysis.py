
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib
from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split
import tensorflow as tf


# In[23]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_classification_utils as mclfu
import my_steven_lab_utils as mslabu
import my_tensorflow_cnn_utils as mcnnu
import my_tensorflow_lstm_utils as mlstmu
import my_tensorflow_dense_utils as mdenseu
import generate_result_utils as gresu
#importlib.reload(biteu)


# In[3]:


axis_count, label_shape_1 = 9, 1 
win_size, step_size, feature_count = 6*16, 2, 32
var_min, var_max, gx_th = 1, 25, -0.25
print("Win size:", win_size, ", Step size:", step_size, ", var min:", var_min, ", var max:", var_max, ', gx_th:', gx_th)


# In[5]:


dlab = mfileu.read_file('data', 'lab_data_steven_smoothed.pkl')
dlab, _, dlab_annots = mslabu.separate_right_left_annots(dlab)
dfree = mfileu.read_file('data', 'free_data_steven_right_smoothed.pkl')
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
fs = mfileu.read_file('features', 'free_features_steven_right.pkl')


# In[25]:


all_proba_steven = gresu.get_all_proba(annots, 'baseline_test_proba_bite_steven', 'bite_free', sess_avail=True)
all_proba_our = gresu.get_all_proba(annots, 'our_test_proba_bite_6s', 'bite_free', sess_avail=True)


# In[7]:


def get_mid_gx(ds, indices, win_size):
    count = len(indices)
    x = np.zeros((count, ))    
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]+win_size//2
        x[i] = ds[subj][sess][ix, -3]                
    return x    


# In[8]:


def get_variance_accel(ds, indices, win_size):   
    count = len(indices)
    v = np.zeros((count, ))    
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]
        v[i] = np.sum(np.var(ds[subj][sess][ix:ix+win_size, 1:4], axis=0))                
    return v


# In[9]:


def get_labels_lab(indices, a, win_size):
    a = a[a[:, 3]!=2, :] # right hand only    
    wcount, acount = len(indices), len(a)  
    
    si = indices
    q1 = si + win_size//4
    mi = si + 2*win_size//4    
    q3 = si + 3*win_size//4    
    
    labels=np.zeros((wcount,))    
    for i in range(acount):         
        if a[i, 2]==1: #bite
            ix = a[i, 0]
            cond = (q1<=ix) & (ix<=q3)
            label = 1
        else:#sip
            ix1 = a[i, 0]
            ix2 = a[i, 1]
            cond = (mi>=ix1) & (mi<=ix2)
            label = 2
        
        labels[cond] = label
    return np.array(labels)


# In[10]:


def get_labels_free(indices, a, win_size):    
    wcount, acount = len(indices), len(a)      
    mi = indices + win_size//2    
    
    labels=np.zeros((wcount,))    
    for i in range(acount):                 
        ix1 = a[i, 0]
        ix2 = a[i, 1]
        cond = (mi>=ix1) & (mi<=ix2)
        labels[cond] = a[i, 2]
        
    return labels.astype(int)
    


# In[17]:


def get_all_indices_lab(exclude_subj=-1):
    all_indices = np.zeros((0, 4))
    for subj in range(len(dlab)):
        if subj==exclude_subj:
            continue
        for sess in range(len(dlab[subj])):
            d = dlab[subj][sess]
            a = dlab_annots[subj][sess]

            count = (len(d)-win_size)//step_size + 1
            indices = np.array(list(range(count)))*2
            labels = get_labels_lab(indices, a, win_size)
            labels[labels==2] = 0

            ix = np.zeros((count, 4))
            ix[:, 0] = subj
            ix[:, 1] = sess
            ix[:, 2] = indices
            ix[:, 3] = labels
            all_indices = np.concatenate((all_indices, ix))

    all_indices = all_indices.astype(int)
    assert np.sum(all_indices[:, -1]>1) == 0
    assert np.sum(all_indices[:, -1]<0) == 0
    #print("All indices shape, neg, bite:", len(all_indices), np.sum(all_indices[:, -1]==0), np.sum(all_indices[:, -1]==1))
    return all_indices


# In[18]:


def get_subject_indices_free(subj):
    all_indices = np.zeros((0, 4))    
    for sess in range(len(dfree[subj])):
        d = dfree[subj][sess]
        a = annots[subj][sess]
        
        count = (len(d)-win_size)//step_size + 1
        indices = np.array(list(range(count)))*2
        labels = get_labels_free(indices, a, win_size)
        labels[labels==2] = 1
        labels[labels==3] = 0
        
        ix = np.zeros((count, 4))
        ix[:, 0] = subj
        ix[:, 1] = sess
        ix[:, 2] = indices
        ix[:, 3] = labels
        all_indices = np.concatenate((all_indices, ix))
        
    
    all_indices = all_indices.astype(int)
    assert np.sum(all_indices[:, -1]>1) == 0
    assert np.sum(all_indices[:, -1]<0) == 0
    #print("All indices shape, neg, pos:", len(all_indices), np.sum(all_indices[:, -1]==0), np.sum(all_indices[:, -1]==1))
    return all_indices


# In[19]:


def get_all_indices_free(exclude_subj=-1):
    all_indices = np.zeros((0, 4))
    
    for subj in range(len(dfree)):
        if subj == exclude_subj:
            continue
        for sess in range(len(dfree[subj])):
            d = dfree[subj][sess]
            a = annots[subj][sess]

            count = (len(d)-win_size)//step_size + 1
            indices = np.array(list(range(count)))*2
            labels = get_labels_free(indices, a, win_size)
            labels[labels==2] = 1
            labels[labels==3] = 0

            ix = np.zeros((count, 4))
            ix[:, 0] = subj
            ix[:, 1] = sess
            ix[:, 2] = indices
            ix[:, 3] = labels
            all_indices = np.concatenate((all_indices, ix))
            
    all_indices = all_indices.astype(int)
    assert np.sum(all_indices[:, -1]>1) == 0
    assert np.sum(all_indices[:, -1]<0) == 0
    #print("All indices shape, neg, bite:", len(all_indices), np.sum(all_indices[:, -1]==0), np.sum(all_indices[:, -1]==1))
    return all_indices


# In[28]:


for subj in range(len(fs)):
    subj_indices = get_subject_indices_free(subj)
    for sess in range(len(fs[subj])):        
        indices = subj_indices[subj_indices[:,1]==sess, :]
        print(subj, sess, " : ", indices.shape, all_proba_steven[subj][sess].shape, all_proba_our[subj][sess].shape)        
        print("Shapes indices, feature_count: ",indices.shape, fs[subj][sess].shape)        
        print("Shapes proba steven, our: ",all_proba_steven[subj][sess].shape, all_proba_our[subj][sess].shape)        
        
        gx = get_mid_gx(dfree, indices, win_size=win_size)
        indices = indices[gx<=gx_th]    
        print("Indices summary after gx filter total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))

        v = get_variance_accel(dfree, indices, win_size=win_size)            
        indices = indices[(v>=var_min) & (v<=var_max)]   
        print("Indices summary after variance filter total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))

