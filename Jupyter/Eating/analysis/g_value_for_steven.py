
# coding: utf-8

# In[20]:


import numpy as np
import pickle
import os
import sys


# In[21]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfu
import my_data_process_utils as mdpu


# In[42]:


def find_min_variance(d, window_size):
    #print(d.shape)
    d = mdpu.smooth_data(d, 0.9)
    min_var = np.inf
    g = 0
    for i in range(0, len(d)-window_size, window_size//2):
        w = d[i:i+window_size, :]
        v = np.sum(np.var(w, axis=0))
        if v<min_var:
            min_var = v
            g = np.mean(np.sqrt(np.sum(w*w, axis=1)))
            #print('{}, {:.2f}, {:.2f}'.format(i, v, g), end="; ")
            
        #if i%1000==0:
        #    print(i, end=": ")
            
    return min_var, g


# In[43]:


#read_data_steven_lab

path = 'C:/ASM/PublicData/eating_steventech/lab'
sampling_rate = 16 #Hz
data = []

all_gs = []
for subj in range(7):    
    for sess in range(2):
        if subj==1 and sess==1:
            continue
        
        #print("====== Right Hand =====")
        filepath = path + "/0" + str(subj) + "/000" + str(sess) + "/watch_right_000" + str(sess) + ".csv"
        d = np.genfromtxt(filepath, delimiter=',')
        rmv, rg = find_min_variance(d[:, 1:4], 15)
        
        
            
        #print("====== Left Hand =====")
        filepath = path + "/0" + str(subj) + "/000" + str(sess) + "/watch_left_000" + str(sess) + ".csv"
        d = np.genfromtxt(filepath, delimiter=',')
        lmv, lg = find_min_variance(d[:, 1:4], 15)
        
        print(subj, sess, " : ", rg, lg," : ", rmv, lmv)        
        all_gs.append(rg)
        all_gs.append(lg)

