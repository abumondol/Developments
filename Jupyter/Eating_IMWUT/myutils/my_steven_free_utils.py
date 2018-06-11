
# coding: utf-8

# In[1]:


import numpy as np


# In[2]:


def process_anntos(dcount, a):
    acount = len(a)
    for i in range(acount):
        if a[i, 0]>=dcount:
            #print("Annots cut at meal: {}/{}, Times data, start: {}, {}".format(i+1, acount, dcount//16, a[i,0]//16))
            a = a[:i, :]            
            break
            
        if a[i, 1]>=dcount:
            #print("Annots end adjusted at meal: {}/{}, Times data, end: {}, {}".format(i+1, acount, dcount//16, a[i,0]//16))
            a[i, 1]=dcount-1
            a = a[:i+1, :]            
            break
    return a

