
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import generate_result_utils as gresu

import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
importlib.reload(gresu)


# In[3]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
ds = mfileu.read_file('data', 'free_data_steven_right.pkl')
fs = mfileu.read_file('features', 'free_features_steven_right.pkl')


# In[4]:


all_proba_steven = gresu.get_all_proba(annots, 'baseline_test_proba_bite_steven', 'bite_free', sess_avail=True)
pct_proba_steven = gresu.get_percentile_proba_all(all_proba_steven, fs)


# In[5]:


all_proba_our = gresu.get_all_proba(annots, 'our_test_proba_bite', 'bite_free', sess_avail=True)
pct_proba_our = gresu.get_percentile_proba_all(all_proba_our, fs)


# s=0
# mf = 100000
# for p in pct_proba_our:
#     if p[0]!=s:
#         print()
#         s = p[0]
#     if p[2]!=99.9:
#         continue
#     
#     print(int(p[0]), int(p[1]), int(p[2]*100)/100, int(p[3]*mf)/mf, int(p[4]*mf)/mf)

# pos, tot = 0, 0
# for subj in range(len(fs)):
#     for sess in range(len(fs[subj])):
#         pos +=all_proba_our[subj][sess].shape[0]
#         tot += fs[subj][sess].shape[0]
#         print(all_proba_our[subj][sess].shape, fs[subj][sess].shape)
# print(pos, tot, pos/tot)
# 

# In[6]:


def get_tpfp(a, indices):
    if len(indices)==0:
        return 0, 0
    
    a = a[a[:, 2]<=2] #discard meals
    assert len(a)>=1
    
    tps = np.zeros((len(indices), ))    
    for i in range(len(a)):
        si, ei = a[i, 0], a[i, 1]
        cond = (indices[:, 0]>=si) & (indices[:, 1]<=ei)
        tps[cond] = 1
        
    tp = np.sum(tps)
    fp = len(indices) - tp
    return tp, fp    


# In[9]:


#for percentile proba th
def get_tpfp_all_percentile(all_proba, percentile, offline=True, steven=False):
    #print("Percentile: ", percentile)
    tp_count, fp_count = 0, 0
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            a = annots[subj][sess]        
            a = msfreeu.process_anntos(len(ds[subj][sess]), a)
            
            proba = all_proba[subj][sess]            
            if steven:            
                indices = proba[:, 0] + 40
                pct_proba = pct_proba_steven
            else:
                indices = proba[:, -2] + 48                
                pct_proba = pct_proba_our
                
                
            cond = (pct_proba[:, 0]==subj) & (pct_proba[:, 1]==sess) & (pct_proba[:, 2]==percentile)                
            assert np.sum(cond) == 1
            proba_th = pct_proba[cond, -2] if offline else pct_proba[cond, -1]
            
            proba = proba[:, -1]
            gesture_indices = gresu.detect_gestures(proba, indices, proba_th = proba_th)
                        
            tp, fp = get_tpfp(a, gesture_indices)            
            #print(subj, sess, " : ", len(gesture_indices), tp, fp)
            
            tp_count += tp
            fp_count += fp

    return tp_count, fp_count


# In[10]:


#for fixed proba th
def get_tpfp_all_fixed(all_proba, proba_th, steven=False):
    tp_count, fp_count = 0, 0
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            a = annots[subj][sess]        
            a = msfreeu.process_anntos(len(ds[subj][sess]), a)
                        
            proba = all_proba[subj][sess]
            
            if steven:            
                indices = proba[:, 0] + 40
            else:
                indices = proba[:, -2] + 48
                
            proba = proba[:, -1]                        
            gesture_indices = gresu.detect_gestures_fixed_th(proba, indices, proba_th = proba_th)
                        
            tp, fp = get_tpfp(a, gesture_indices)            
            #print(subj, sess, " : ", len(gesture_indices), tp, fp)
            
            tp_count += tp
            fp_count += fp

    return tp_count, fp_count


# In[ ]:


N=100
F=6
H=12
res = N*F/(2*H*3600-2*F)
print(res)


# In[ ]:


res = []
for proba_th in range(1, 100):    
    tp1, fp1 = get_tpfp_all_fixed(all_proba_our, proba_th/100)
    tp2, fp2 = get_tpfp_all_fixed(all_proba_steven, proba_th/100, steven=True)
    print(proba_th, "TP, FP Our: {}, {},  Steven: {}, {}".format(tp1, fp1, tp2, fp2))


# In[11]:


res = []
for p in range(9980, 10000, 2):
    percentile = p/100
    print(percentile)
    tp1, fp1 = get_tpfp_all_percentile(all_proba_our, percentile, offline=True)
    tp2, fp2 = get_tpfp_all_percentile(all_proba_steven, percentile, steven=True, offline=True)
    print(percentile, "TP, FP Our: {}, {},  Steven: {}, {}".format(tp1, fp1, tp2, fp2))

    ftr1 = fp1/tp1
    ftr2 = fp2/tp2
    print(ftr1, ftr2, 1-ftr1/ftr2)    
    res.append([percentile, tp1, fp1, tp2, fp2])    
    


# In[ ]:


res = np.array(res)
fig = plt.figure(figsize=(10,6))
ax = plt.subplot(111)  

ax.plot(res[:,0], res[:, 1], label= 'TP (Our)', color='green')
ax.plot(res[:,0], res[:, 2], label= 'FP (Our)', color='red')
ax.plot(res[:,0], res[:, 3], label= 'TP (Baseline)', linestyle='--', color='green')
ax.plot(res[:,0], res[:, 4], label= 'FP (Baseline)', linestyle='--', color='red')

#plt.title(" ", fontsize=20)
plt.xlabel("Percentile", fontsize=20)        
plt.ylabel("Count", fontsize=20)            
plt.legend(fontsize=16)
plt.grid(True)
plt.show()       

ftr1 = res[:, 2]/res[:,1]
ftr2 = res[:, 4]/res[:,3]

fig = plt.figure(figsize=(10,6))
ax = plt.subplot(111)  

ax.plot(res[:,0], ftr1, label= 'FTR (Our)', color='blue')
ax.plot(res[:,0], ftr2, label= 'FTR (Baseline)', linestyle='--', color='blue')


#plt.title(" ", fontsize=20)
plt.xlabel("Percentile", fontsize=20)        
plt.ylabel("FTR", fontsize=20)            
plt.legend(fontsize=16)
plt.grid(True)
plt.show()


# In[ ]:


p = pct_proba_our
p = p[p[:, 2]==99.9, :]
plt.hist(p[:, -2], bins=10, edgecolor='black', hatch='/', label='Our', color='green', alpha=0.5,)

p = pct_proba_steven
p = p[p[:, 2]==99.9, :]
plt.hist(p[:, -2], bins=10, edgecolor='black', hatch='+', label='Baseline', color='blue', alpha=0.5,)

plt.title("Histogram (99.9 percentile probability)", fontsize=14)
plt.xlabel('Probabllity', fontsize=14)
plt.ylabel('Count', fontsize=14)
plt.grid()
plt.legend(fontsize=14)
plt.xlim([0, 1])
plt.show()

