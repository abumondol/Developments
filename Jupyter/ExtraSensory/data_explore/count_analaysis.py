
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys


# In[4]:


path = 'C:/ASM/DevData/extrasensory/data/'
with open(path+'features_labels.pkl', 'rb') as file:
    data = pickle.load(file)
    
with open(path+'features_labels_column_names.pkl', 'rb') as file:
    columns = pickle.load(file)


# In[6]:


subj_count = len(data)
print(subj_count)
print(columns[100])


# In[16]:


l = np.zeros((subj_count, ))
for i in range(subj_count):
    l[i] =  data[i].shape[0]

print(np.sum(l), np.min(l), np.max(l), np.mean(l))


# In[11]:


dist = np.zeros((0, ))
for i in range(subj_count):
    d = data[i][1:, 0] - data[i][:-1, 0]
    dist = np.concatenate((dist, d))
    
        


# In[25]:


print(len(dist))

for i in range(0, 1000, 10):
    print(i, np.sum(dist<=i))


# In[22]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


plt.hist(d, bins=100)
plt.xlim([-1, 100])
plt.show()

