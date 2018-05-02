
# coding: utf-8

# In[1]:


import numpy as np
import os
import sys
import importlib


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_feature_utils as mfeatu
import my_steven_free_utils as msfreeu
import my_steven_lab_utils as mslabu
import my_segmentation_utils as msegu
#importlib.reload()


# In[8]:


def get_window_indices_all(ds, annots, blockPrint=True):    
    if blockPrint:
        old_stdout = sys.stdout
        sys.stdout = open(os.devnull, 'w')
    
    print("Generating window indices...")
    windows = np.empty((0, 5))
    for subj in range(len(ds)):
        for sess in range(len(ds[subj])):
            print("\nSubject, Session: ", subj, sess)
                        
            print("Session Shapes window, labels: ", w.shape, np.sum(w[:,4]==0), np.sum(w[:,4]==1), np.sum(w[:,4]==2), np.sum(w[:,4]==3))
            print("---------------------------------------------")
        
    if blockPrint:
        sys.stdout.close()        
        sys.stdout = old_stdout
        
    return windows.astype(int)


# In[7]:


def get_window_data(ds, indices, win_size, reverse=False):   
    count = len(indices)
    w = np.zeros((count, win_size, 9))
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]
        d = ds[subj][sess][ix:ix+win_size, 4:]
        if reverse:
            d = np.flip(d, axis=0)
        w[i, :, :] = d        
    return w

