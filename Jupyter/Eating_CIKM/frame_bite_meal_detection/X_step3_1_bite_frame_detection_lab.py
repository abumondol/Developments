
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import bite_detection_utils as bdu
import copy


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
#importlib.reload(mdu)


# In[3]:


annots = mfileu.read_file('data', 'lab_annots_steven_right.pkl')
ssilv = mfileu.read_file('ssilv2', 'lab_ssilv_steven_right.pkl')


# In[11]:


RF_our ='RF'
all_proba = mfileu.read_file('all_proba2', 'all_proba_frame_lab_{}.pkl'.format(RF_our))
pct_proba = mfileu.read_file('pct_proba2', 'pct_proba_frame_lab_{}.pkl'.format(RF_our))


# In[6]:



def check_bites(a, indices):        
    ix = indices
    gt = a[a[:,2]==1, 0]
    gt_count = len(gt)
    ix_count = len(ix)
    
    tp, fp, fn = 0, 0, 0    
    if ix_count==0:
        fn = gt_count
        return tp, fp, fn
    
    
    
    tp_ix = np.zeros((ix_count, ))
    for i in range(gt_count):
        d = np.absolute(ix-gt[i])
        mi = np.argmin(d)
        if d[mi]<=gt_distance:
            tp_ix[mi] = 1
        
    tp = np.sum(tp_ix)
    fp = ix_count - tp
    fn = gt_count - tp
                
    return tp, fp, fn


# In[7]:


gt_distance = 2*16
def check_bites_old(a, indices):    
    tp, fp, tn, fn = 0, 0, 0, 0
    if len(indices)==0:
        return tp, fp, fn
    
    ix = (indices[:,0]+indices[:,1])/2
    ix = ix.astype(int)    
    gt = a[a[:,2]==1, 0]
    gt_count = len(gt)
    ix_count = len(ix)
    
    for i in range(ix_count):
        cond = np.absolute(gt - ix[i])<=gt_distance        
        if np.sum(cond)>0:
            tp += 1
        else:
            fp += 1
        
    for i in range(gt_count):
        cond = np.absolute(gt[i] - ix)<=gt_distance        
        if np.sum(cond)==0:
            fn +=1 
            
    return tp, fp, fn


# In[8]:


def get_bites_frames(bite_frame, percentile_proba, percentile_proba_val, off_on=None):
    assert off_on in [None, "offline", "online"]
    assert bite_frame in ["bite", "frame"]    
    assert percentile_proba in ["percentile", "proba"]    
    
    ba = mfileu.read_file('data', 'lab_data_steven_blank_array.pkl')
    tp, fp, tn, fn = 0, 0, 0, 0
    
    
    
    for subj in range(len(ba)):
        for sess in range(len(ba[subj])):
            a = annots[subj][sess]            
            ix_proba = all_proba[subj][sess][:, :2]
            
            if percentile_proba=='percentile':                
                cond  = (pct_proba[:, 0]==subj) & (pct_proba[:, 1]==sess) & (pct_proba[:, 2]==percentile_proba_val)
                assert np.sum(cond)==1                        
                proba_th = pct_proba[cond, -2] if off_on=="offline" else pct_proba[cond, -1]
            else:
                proba_th = percentile_proba_val
            
            if bite_frame=="bite":
                indices = bdu.detect_bites(ix_proba, proba_th=proba_th, ix_offset=40)
                ba[subj][sess] = indices                
                tp_1, fp_1, fn_1 = check_bites(a, indices)
                #print(tp_1, fp_1, fn_1)
                
            else:
                #ix = ix_proba[:, 0]+40
                proba = ix_proba[:, 1]            
                #indices = ix[proba>=proba_th].reshape((-1, 1))
                pred = proba>=proba_th                
                gt = (ssilv[subj][sess][:, 3]==1)
                                
                tp_1 = np.sum(pred & gt)
                fp_1 = np.sum(pred & np.logical_not(gt))
                fn_1 = np.sum(np.logical_not(pred) & gt)
                tn_1 = np.sum(np.logical_not(pred) & np.logical_not(gt))
                
                #print(tp_1, fp_1, fn_1, tn_1, len(pred), len(gt))
                assert tp_1 + fp_1 + fn_1 + tn_1 == len(pred)
                
            
            tp += tp_1
            fp += fp_1            
            fn += fn_1
    
    res = {"list":ba, "tp":tp, "fp":fp, "fn":fn}
    #res = {"tp":tp, "fp":fp, "fn":fp}
    return res


# In[9]:


res ={}
for p in range(9800, 10000):
    percentile = p/100    
    print(RF_our, percentile, end=" : ")    
    x = {}
    x["bite_offline"] = get_bites_frames("bite", "percentile", percentile, "offline")    
    #x["frame_offline"] = get_bites_frames("frame", "percentile", percentile, "offline")    
    res[percentile] = x
    
mfileu.write_file('all_bites_frames_lab', 'all_bites_frames_percentile_{}.pkl'.format(RF_our), res)


# In[12]:


res ={}
for p in range(10, 95, 5):
    proba = p/100    
    print(RF_our, proba, end=" : ")    
    x = {}
    x["bite"] = get_bites_frames("bite", "proba", proba)    
    #x["frame"] = get_bites_frames("frame", "proba", proba)    
    res[proba] = x
    
    #print(RF_our, proba)
    #print(x["bite"]["tp"], x["bite"]["fp"], x["bite"]["fn"])
    
mfileu.write_file('all_bites_frames_lab', 'all_bites_frames_proba_{}.pkl'.format(RF_our), res)

