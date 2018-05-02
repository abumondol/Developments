
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import Bite_Utils as biteu
import my_feature_utils as mfeatu
import my_steven_lab_utils as mslabu
import my_steven_free_utils as msfreeu
importlib.reload(biteu)


# In[3]:


hand = 'right'
win_size = 3*16
varth, xth = 0.5, -0.3
min_bite_interval, min_stay, boundary_offset = 2*16, 8, 5*16
label_max_distance, label_buffer_distance = 2*16, 4*16


# In[4]:


dsa = mfileu.read_file('data', 'lab_data_steven.pkl')
ds_right, ds_left, annots = mslabu.separate_right_left_annots(dsa)
annots = mslabu.adjust_annots_all(annots)


# In[5]:


ds = ds_right  if hand=='right' else ds_left    
annots = mslabu.filter_annots_all(annots, hand=hand)


# In[6]:


mps, acs = biteu.get_min_points_all(ds, annots, 
                                    xth = xth, 
                                    min_bite_interval=min_bite_interval,
                                    min_stay = min_stay,
                                    boundary_offset = boundary_offset,
                                    label_max_distance=label_max_distance,
                                    label_buffer_distance = label_buffer_distance,
                                    block_print = False)
biteu.print_label_summary(mps, acs)


# In[7]:


#windows = mwgenu.get_window_data(ds, ixf, win_size=win_size)
#print(windows.shape)


# In[8]:


ix = mps[:, :3]
ix[:, 2] = ix[:, 2]-40
v = mfeatu.get_variance_accel(ds, ix, win_size=5*16)


# In[12]:


cond = (v>=0.5)
print(mps.shape, v.shape, cond.shape, np.sum(cond))
mps2 = mps[cond, :]
biteu.print_label_summary(mps2, acs)


# In[14]:


a = 5
assert a==True

