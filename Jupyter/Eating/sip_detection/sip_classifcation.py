
# coding: utf-8

# In[8]:


import numpy as np
import pickle
import sys
import os
import importlib


# In[20]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_segmentation_utils as msegu
import my_steven_lab_utils as mslabu
importlib.reload(msegu)


# In[21]:


ds = mfileu.read_file('data', 'lab_data_steven.pkl')


# In[30]:


for subj in range(7):
    for sess in range(len(ds[subj])):
        data = ds[subj][sess]["data_right"]
        annots = ds[subj][sess]["annots"]
        annots = mslabu.filter_annots(annots, 'all', 'right', filter_ambigous=True)
        
        mps = msegu.find_min_points_by_xth(data[:, -3], xth=-0.4, min_bite_interval=2*16, filter_by_neighbor=True)
        segs = 
        print(subj, sess, ":", len(mps), len(annots))
        
        

