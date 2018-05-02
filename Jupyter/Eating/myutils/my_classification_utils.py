
# coding: utf-8

# In[1]:


import numpy as np


# In[1]:


def get_one_hot(labels, num_classes):
    count = len(labels)
    res = np.zeros((count, num_classes), dtype=int)
    res[np.arange(count), labels] = 1
    return res


# In[2]:


def adjust_for_batch_size(train_x, train_y, batch_size):
    train_count = len(train_x)    
    last_batch_size = train_count%batch_size
    if last_batch_size >0:
        last_batch_gap = batch_size - last_batch_size        
        train_x = np.concatenate((train_x, train_x[0:last_batch_gap]), axis=0)
        train_y = np.concatenate((train_y, train_y[0:last_batch_gap]), axis=0)
        
    train_count = len(train_x)    
    assert train_count%batch_size == 0
    return train_x, train_y


# In[3]:


def get_scores_1d(pred, gt):
    p = np.zeros(pred.shape, dtype=np.int32)
    p[pred>=0.5]=1
    gt[gt>1]=1
    
    tn = np.sum((p==0) & (gt==0))
    tp = np.sum((p==1) & (gt==1))
    fn = np.sum((p==0) & (gt==1))
    fp = np.sum((p==1) & (gt==0))
    
    acc = (tp+tn)/(tp+tn+fp+fn)
    pr = tp/(tp+fp)
    rc = tp/(tp+fn)
    f1 = 2*pr*rc/(pr+rc)
    
    
    return tn, tp, fn, fp, acc, pr, rc, f1

