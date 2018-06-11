
# coding: utf-8

# In[1]:


import numpy as np
import os 
import sys
import importlib
import copy


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[3]:


ssilp = mfileu.read_file('classification_results', 'free_ssilp_baseline.pkl')
ba = mfileu.read_file('data', 'free_data_steven_blank_array.pkl')


# In[4]:


def detect_bites_steven(ix, proba, proba_th):    
    count = len(ix)
    res = []
    inside = False
    for i in range(count):
        if proba[i] >= proba_th and inside==False:
            inside = True
            si = i
        elif proba[i]<proba_th/2 and inside==True:            
            ei = i-1
            bix = (ix[si]+ix[ei])//2
            res.append(bix)
            inside = False
        
    res = np.array(res).astype(int)    
    return res    


# In[5]:


for subj in range(len(ba)):
    for sess in range(len(ba[subj])):        
        ix = ssilp[subj][sess][:, 2]+40
        proba = ssilp[subj][sess][:, -1]
        proba_th = np.percentile(proba, 99.9)
        
        bites = detect_bites_steven(ix, proba, proba_th)
        print(subj, sess, " : ", ssilp[subj][sess].shape, proba_th, bites.shape)
        
        ba[subj][sess] = bites       
        


# In[6]:


mfileu.write_file('bites', 'bites_baseline.pkl', ba)

