
# coding: utf-8

# In[ ]:


import numpy as np


# In[ ]:


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

