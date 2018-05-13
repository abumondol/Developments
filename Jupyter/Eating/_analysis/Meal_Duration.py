
# coding: utf-8

# In[5]:


import numpy as np
import os
import sys


# In[7]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_lab_utils as mslabu
#importlib.reload(mdu)


# In[8]:


a = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[9]:


meals = np.zeros((0, 3))
for subj in range(len(a)):
    for sess in range(len(a[subj])):
        meals = np.concatenate((meals, a[subj][sess]))


# In[16]:


print(meals.shape)
for i in range(1, 4):
    print(i, np.sum(meals[:,-1]==i))


# In[ ]:


m = meals[meals[:, 2]==1, :]
d = (m[:, 1] - m[:, 0]+1)/(16*60)
d = np.sort(d)
print(d[:10])


# In[15]:


print(meals.shape)
m = meals[meals[:, 2]==2, :]
d = (m[:, 1] - m[:, 0]+1)/(16*60)
d = np.sort(d)
print(d[:10])

