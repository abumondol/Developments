
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[ ]:


lab_free = 'lab'
assert lab_free in ['lab', 'free']
  
features = mfileu.read_file('features/features_all_samples', '{}_features_steven_right.pkl'.format(lab_free))   
ssilvg = mfileu.read_file('ssilvg/ssilvg_all_samples', '{}_ssilvg_steven_right.pkl'.format(lab_free))   


# In[ ]:


step = 2
for subj in range(len(ssilvg)):
    for sess in range(len(ssilvg[subj])):
        print(subj, sess)
        features[subj][sess] = features[subj][sess][::step, :]
        ssilvg[subj][sess] = ssilvg[subj][sess][::step, :]
        assert len(features[subj][sess]) == len(ssilvg[subj][sess])
        assert np.sum(features[subj][sess][:,0] - ssilvg[subj][sess][:,2]) == 0


# In[ ]:


mfileu.write_file('features/features_step'+str(step), '{}_features_steven_right.pkl'.format(lab_free), features)   
mfileu.write_file('ssilvg/ssilvg_step'+str(step), '{}_ssilvg_steven_right.pkl'.format(lab_free), ssilvg)    

