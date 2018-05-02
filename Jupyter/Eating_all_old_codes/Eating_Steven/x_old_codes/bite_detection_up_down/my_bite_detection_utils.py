
# coding: utf-8

# In[1]:


import numpy as np


# In[2]:


def get_segments(x, th_up, th_down):    
    count = len(x)    
    segs = []    
    s = 1
    while s<count and x[s]<=th_down:
        s+=1
        
    inside = False    
    for i in range(s,count):
        if x[i-1]>th_up>=x[i] and inside == False:
            s, inside =i, True            
        elif x[i-1]<=th_down<x[i] and inside == True:
            e, inside = i-1, False
            segs.append([s, e])
        i+=1
            
    segs = np.array(segs)
    return segs

def get_covers(segs, annots, offset):
    seg_count = len(segs)
    covers = np.zeros((seg_count, 2))
    for i in range(seg_count):
        s, e = segs[i, 0], segs[i, 1]
        cond = (annots[:, 0]>=s-offset) & (annots[:, 0]<=e+offset) 
        condb = (annots[:, 1]==1)
        conds = (annots[:, 1]==2)
        covers[i, 0] = np.sum(cond & condb)
        covers[i, 1] = np.sum(cond & conds)        
    return covers

def get_sub_sess(subj, sess, rows, cols, dtype=np.float32):
    sub_sess = np.zeros((rows, cols), dtype=dtype)
    sub_sess[:, 0] = subj
    sub_sess[:, 1] = sess
    return sub_sess

