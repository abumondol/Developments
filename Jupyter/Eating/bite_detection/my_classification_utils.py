
# coding: utf-8

# In[1]:


import numpy as np


# In[2]:


def get_metrics(pred, gt):
    ypr = (pred[:,0]<0.5).astype(int)
    ygt = (gt[:, 0]==0).astype(int)
    tn = np.sum((ypr==0) &(ygt==0))
    tp = np.sum((ypr==1) &(ygt==1))
    fn = np.sum((ypr==0) &(ygt==1))
    fp = np.sum((ypr==1) &(ygt==0))
    pr = tp/(tp+fp)
    rc = tp/(tp+fn)
    sp = tn/(tn+fp)
    f1 = 2*pr*rc/(pr+rc)
    
    return tn, tp, fn, fp, pr, rc, sp, f1


# In[ ]:


def smooth(data, factor):
    count = len(data)
    res = np.copy(data)
    for i in range(1, count):
        res[i, :] = factor*data[i-1, :] + (1-factor)*data[i, :]
    return res

