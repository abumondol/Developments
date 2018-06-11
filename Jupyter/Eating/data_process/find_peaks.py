
# coding: utf-8

# In[4]:


import numpy as np
import pickle
import os
import sys


# In[5]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[6]:


v_min, v_max = 1, 25
g_min, g_max = -10, -2
min_distance = 2*16
win_size = 10*16

lab_free = 'lab'


# In[7]:


def find_labels_labs(gt, peaks):
    pass


# In[8]:


def find_labels_free(gt, peaks):
    pass


# In[10]:


def find_peaks(g):        
    gl, gm, gr = g[:-2], g[1:-1],  g[2:]    
    cond1 = (gm<gl) & (gm<gr) 
    cond2 = (gm>=g_min) & (gm<=g_max)
    cond = cond1 & cond2
    ix = np.nonzero(cond) + 1
    count = len(ix)
    
    i = 0
    while i<count and ix[i]<win_size//2:
        i+=1
        
    j = count-1
    while j>=0 and ix[j]>(count-1)-win_size//2:
        j-=1
        
    ix = ix[i:j]        
    return ix


# In[13]:


ds = mfileu.read_file('data', '{}_data_steven_right_smoothed.pkl'.format(lab_free))
ba = mfileu.read_file('data', '{}_data_steven_blank_array.pkl'.format(lab_free))
if lab_free:
    annots = mfileu.read_file('data', '{}_annots_steven_right.pkl'.format(lab_free))
else:
    annots = mfileu.read_file('data', '{}_annots_steven_processed.pkl'.format(lab_free))


# In[ ]:


peaks = ba
for subj in range(len(ds)):
    for sess in range(len(ds[subj])):
        d = ds[subj][sess]
        a = annots[subj][sess]
        
        

