
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys

import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


# In[25]:


fig = plt.figure(figsize=(12,6))
ax = plt.subplot(111)          

plt.plot([0,1.0], [1, 1], color='blue', linewidth=2)

for i in range(0,11):
    plt.plot([i/10,i/10], [0, 10], color='black', alpha=0.2, linewidth=1)

posy = np.array([0, 0, 0])
negy = np.array([0, 0, 0, 0, 0, 0, 0])

pos = np.array([0.7, 0.8, 0.95])
neg = np.array([0.0, 0.05, 0.07, 0.1, 0.12, 0.15, 0.37])


plt.scatter(pos, posy+1, marker='o', color='green')
plt.scatter(neg, negy+1, marker='x', color='red')
    
ax.set_yticklabels( () )
plt.ylim([0, 10])
plt.show()

