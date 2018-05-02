
# coding: utf-8

# In[1]:


import numpy as np


# In[ ]:


def adjust_annots(annots, subj, sess):
    a = annots
    if subj==0 and sess==1:
        cond = a[:,0]>=9508
        a[cond, 0] = a[cond, 0] + int(1.6*16) #add offset
        a[cond, 1] = a[cond, 1] + int(1.6*16) #add offset

    if subj==2 and sess==0:
        cond = a[:,0]>=9215
        #a = a[cond, :] 

    if subj==5 and sess==1:
        cond = a[:,0]>=11717
        a[cond, 0] = a[cond, 0] + int(1.3*16) #add offset
        a[cond, 1] = a[cond, 1] + int(1.3*16) #add offset
        
    return a

