
# coding: utf-8

# In[1]:


import numpy as np


# In[2]:


def adjust_annots(annots, subj, sess):
    a = np.copy(annots)
    if subj==0 and sess==1:
        print('Subject {}, Sess {} annots adjusted'.format(0, 1))
        cond = a[:,0]>=9508*16
        a[cond, 0] = a[cond, 0] + int(1.6*16) #add offset
        a[cond, 1] = a[cond, 1] + int(1.6*16) #add offset

    if subj==2 and sess==0:
        cond = a[:,0]>=9215*16
        #a = a[cond, :] 

    if subj==5 and sess==1:
        print('Subject {}, Sess {} annots adjusted'.format(5, 1))
        cond = a[:,0]>=11717*16
        a[cond, 0] = a[cond, 0] + int(1.3*16) #add offset
        a[cond, 1] = a[cond, 1] + int(1.3*16) #add offset        
        
    return a


# In[4]:


def adjust_annots_all(annots):
    import copy
    annots = copy.copy(annots)
    subj, sess = 0, 1
    annots[subj][sess] = adjust_annots(annots[subj][sess], subj, sess)
    
    subj, sess = 5, 1
    annots[subj][sess] = adjust_annots(annots[subj][sess], subj, sess)    
        
    return annots


# In[3]:


def filter_annots(annots, bite_sip=None, hand=None, filter_ambigous=False):    
    cond1 = np.full((annots.shape[0], ), True)
    cond2 = np.copy(cond1)
    cond3 = np.copy(cond1)
    
    if bite_sip=='bite':
        cond1 = annots[:, 2]==1
    elif bite_sip=='sip':
        cond1 = annots[:, 2]==2

    assert bite_sip in ['bite', 'sip', None]
    
    if hand=='right':
        cond2 = annots[:, 3]!=2
    elif hand=='left':
        cond2 = annots[:, 3]>=2
    elif hand=='both_only':
        cond2 = annots[:, 3]==3
        
    assert hand in ['right', 'left', 'both_only', None]
    
    if filter_ambigous:
        cond3 = annots[:, 4]==0
        
    annots = annots[cond1&cond2&cond3, :]
    return annots

def filter_annots_all(annots, bite_sip=None, hand=None, filter_ambigous=False):
    import copy
    a = copy.copy(annots) #we do not need deep copy
    for subj in range(len(a)):
        for sess in range(len(a[subj])):            
            a[subj][sess] = filter_annots(annots[subj][sess], bite_sip=bite_sip, hand=hand, filter_ambigous=filter_ambigous)
            
    return a


# In[2]:


def separate_right_left_annots(dsa):
    right, left, annots = [], [], []    
    for subj in range(len(dsa)):
        subj_right, subj_left, subj_annots = [], [], []        
        for sess in range(len(dsa[subj])):
            subj_right.append(dsa[subj][sess]['data_right'])
            subj_left.append(dsa[subj][sess]['data_left'])
            subj_annots.append(dsa[subj][sess]['annots'])
            
        right.append(subj_right)
        left.append(subj_left)
        annots.append(subj_annots)
        
    return right, left, annots

