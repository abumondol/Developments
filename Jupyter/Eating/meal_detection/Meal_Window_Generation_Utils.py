
# coding: utf-8

# In[1]:


import numpy as np
import os
import sys


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import my_feature_utils as mfeatu


# In[2]:


def get_indices(si, ei, step_size):    
    return np.array(list(range(si, ei, step_size))).astype(int)   


# In[3]:


def get_window_indices(dcount, a, neg_step, pos_step):    
    a = msfreeu.process_anntos(dcount, a)            
    acount = len(a)
    print("Meal count: {}, Times data, last meal end: {}, {}".format(acount, dcount//16, a[-1,1]//16))    
    assert a[-1, 1]<=dcount-1        
    print(a)
    
    si, ei = 0, a[0,0]    
    indices = get_indices(si, ei, neg_step)
    windows = np.zeros((indices.shape[0], 3), dtype=np.int32)
    windows[:, 0] = indices        
    
    for i in range(acount):
        si, ei, mt = a[i, 0], a[i, 1], a[i, 2]        
        print('  Meal {}: {} - {}, {}'.format(i+1, si//16, ei//16, mt))
        
        print("    Positives: ")        
        indices = get_indices(si, ei, pos_step)
        w = np.zeros((indices.shape[0], 3), dtype=np.int32)
        w[:, 0] = indices
        w[:, 1] = (ei-si)//16        
        w[:, 2] = mt        
        windows = np.concatenate((windows, w), axis=0)            
        print("\tShapes windows: ", w.shape)

        # Negative windows
        if ei == dcount-1:
            break
            
        print("    Negatives: ")
        si = ei+1
        ei = dcount-1 if i==acount-1 else a[i+1, 0]-1
        
        print("\tTimes: ", si//16, " - " ,ei//16)
        
        indices = get_indices(si, ei, neg_step)
        w = np.zeros((indices.shape[0], 3), dtype=np.int32)
        w[:, 0] = indices    
        windows = np.concatenate((windows, w), axis=0)        
        print("\tShapes window: ", w.shape)        
    
    return windows
   


# In[8]:


def get_train_window_indices_all(ds, annots, win_size, neg_step, pos_step, exclude_subject=-1, blockPrint=True):    
    if blockPrint:
        old_stdout = sys.stdout
        sys.stdout = open(os.devnull, 'w')
    
    print("Generating train window indices...")
    windows = np.empty((0, 5))
    for subj in range(len(ds)):
        if subj==exclude_subject:
            continue
            
        for sess in range(len(ds[subj])):
            print("\nSubject, Session: ", subj, sess)
            dcount = len(ds[subj][sess]) 
            a = annots[subj][sess]
            
            indices = get_window_indices(dcount-win_size, a, neg_step=neg_step, pos_step=pos_step)            
            w = np.zeros((len(indices), 5), dtype=np.int32)
            w[:, 0] = subj
            w[:, 1] = sess
            w[:, 2:] = indices
            windows = np.concatenate((windows, w), axis=0)                    
            print("Session Shapes window, labels: ", w.shape, np.sum(w[:,4]==0), np.sum(w[:,4]==1), np.sum(w[:,4]==2), np.sum(w[:,4]==3))
            print("---------------------------------------------")
        
    if blockPrint:
        sys.stdout.close()
        #sys.stdout = sys.__stdout__
        sys.stdout = old_stdout
        
    return windows.astype(int)


# In[10]:


def get_test_window_indices(ds, annots, win_size, step_size, subj, sess, blockPrint=True):    
    if blockPrint:
        old_stdout = sys.stdout
        sys.stdout = open(os.devnull, 'w')
    
    print("Generating test window indices... subj, sess: ", subj, sess)    
    
    
    dcount = len(ds[subj][sess])    
    a = annots[subj][sess] if len(annots)>0 else []
        
    
    indices = get_indices(0, dcount-win_size, step_size)    
    w = np.zeros((len(indices), 5), dtype=np.int32)    
    w[:, 0] = subj
    w[:, 1] = sess
    w[:, 2] = indices
    
    ix = indices + win_size//2
    for i in range(len(a)):
        cond = (ix>=a[i,0]) & (ix<=a[i,1])
        w[cond, -1] = a[i,2]        
        
    if blockPrint:
        sys.stdout.close()        
        sys.stdout = old_stdout
        
    return w.astype(int)


# hand = 'right'
# ds = mfileu.read_file('data', 'free_data_steven_'+hand+'.pkl')
# annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')

# win_size, neg_step, pos_step = 5*16, 16, 8
# w = get_train_window_indices(ds, annots, win_size=win_size, neg_step=neg_step, pos_step=pos_step, blockPrint=True)
# print("All Shapes window, labels: ", w.shape, np.sum(w[:,4]==0), np.sum(w[:,4]==1), np.sum(w[:,4]==2), np.sum(w[:,4]==3))

# In[5]:


def get_window_data(ds, indices, win_size):   
    count = len(indices)
    w = np.zeros((count, win_size, 9))
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]
        w[i, :, :] = ds[subj][sess][ix:ix+win_size, 4:]        
    return w

