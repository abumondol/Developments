
# coding: utf-8

# In[2]:


import numpy as np
#import tensorflow as tf


# In[4]:


a = np.argwhere(np.arange(10)>5)
print(a)


# In[40]:


b = np.random.rand(5,)
print(b)


# In[41]:


print(a+b)


# In[16]:


a, b = (2, 4)


# In[17]:


print(a, b)


# In[1]:


a = lambda x: x+2


# In[2]:


print(a(10))


# In[42]:


a = np.array([5, 7, 90])
np.clip(a, 0, 10)


# In[43]:


print(a)


# In[44]:


-a

