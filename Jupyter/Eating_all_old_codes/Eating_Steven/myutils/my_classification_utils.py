
# coding: utf-8

# In[1]:


import numpy as np


# In[ ]:


def get_one_hot(labels, num_classes):
    count = len(labels)
    res = np.zeros((count, num_classes), dtype=int)
    res[np.arange(count), labels] = 1
    return res

