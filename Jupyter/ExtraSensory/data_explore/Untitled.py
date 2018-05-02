
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys


# In[3]:


path = 'C:/ASM/DevData/extrasensory/data/features_labels.pkl'
with open(path, 'rb') as file:
    data = pickle.load(file)


# In[6]:


subj_count = len(data)
print(subj_count)


# In[9]:


l = np.zeros((subj_count, ))
for i in range(subj_count):
    l[i] =  data[i].shape[0]

print(np.sum(l), np.min(l), np.max(l), np.mean(l))


# In[12]:


6289/60

