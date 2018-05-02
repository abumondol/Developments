
# coding: utf-8

# In[31]:


import numpy as np
import pickle
import os
import importlib
import matplotlib.pyplot as plt
import my_bite_detection_utils as bdu


# In[122]:


#importlib.reload(mtu)
importlib.reload(bdu)


# In[53]:


with open('C:/ASM/DevData/eating/data/steven_uva_lab_data_combined.pkl', 'rb') as file:
    ds = pickle.load(file)


# In[177]:


x_th = 0
min_bite_interval = 2*16
window_size = 6*16
max_annot_distance = 3*16

sub_sess_mps, sub_sess_annots,  = np.empty((0, 3), dtype=int), np.empty((0, 4), dtype=int)
mp_covs, annot_covs = np.empty((0, 5), dtype=int), np.empty((0, 5), dtype=int)
x_var_mps = np.empty((0, 2))
x_annots = np.empty((0, 3))
var_annots = np.empty((0, 3))

for subject in range(len(ds)):
    for sess in range(len(ds[subject])):
        print("Subejct, sess: ", subject, sess)
        data = ds[subject][sess]['data']
        annots = ds[subject][sess]['annots']
        
        mps = bdu.find_min_points_by_xth(data[:, 1], x_th, min_bite_interval)
        mps = bdu.remove_min_points_at_boundary(mps, len(data), window_size//2)
        
        
        ssm = np.zeros((len(mps), 3))
        ssm[:, 0] = ssm[:, 0] + subject
        ssm[:, 1] = ssm[:, 1] + sess
        ssm[:, 2] = mps
        sub_sess_mps = np.concatenate((sub_sess_mps, ssm), axis=0)        
        
        ssa = np.zeros((len(annots), 4))
        ssa[:, 0] = ssa[:, 0] + subject
        ssa[:, 1] = ssa[:, 1] + sess
        ssa[:, 2:] = annots
        sub_sess_annots = np.concatenate((sub_sess_annots, ssa), axis=0)        
                
        mp_cov, annot_cov = bdu.get_coverage_stat(mps, annots, max_distance=max_annot_distance)
        mp_covs = np.concatenate((mp_covs, mp_cov), axis=0)
        annot_covs = np.concatenate((annot_covs, annot_cov), axis=0)         
                
        xv = np.zeros((len(mps), 2))        
        xv[:, 0] = data[mps, 1]        #grav x        
        print(np.sum(xv[:, 0]>1))
        w = bdu.get_windows(data[:, 1:], mps, window_size)        
        xv[:, 1] = bdu.get_variance(w, 3, 5) #accel var        
        x_var_mps = np.concatenate((x_var_mps, xv), axis=0)
        
        ac = np.copy(annot_cov)
        ac = ac[:, :3]        
        flags = ac<0
        ac[flags] = 0                
        
        xa = np.zeros((len(annots), 3))        
        xa[:, 0] = data[mps[ac[:, 0]], 1]
        xa[:, 1] = data[mps[ac[:, 1]], 1]
        xa[:, 2] = data[mps[ac[:, 2]], 1]
        xa[flags] = 1
        x_annots = np.concatenate((x_annots, xa), axis=0)
        
        va = np.zeros((len(annots), 3))
        va[:,0] = xv[ac[:,0], 1]
        va[:,1] = xv[ac[:,1], 1]
        va[:,2] = xv[ac[:,2], 1]
        va[flags] = -1
        var_annots = np.concatenate((var_annots, va), axis=0)                


# In[55]:


#Windows
total, neg, bite, sip = len(mp_covs), np.sum(mp_covs[:,0]==0), np.sum(mp_covs[:,0]==1), np.sum(mp_covs[:,0]==2)
print("Windows total, neg, bite, sip: ", total, neg, bite, sip)


# In[56]:


print(np.sum(mp_covs>=1, axis=0))


# In[65]:


#same window covered by different types of annots (both bite and sip)
cond = (mp_covs[:,0]>=1) & (mp_covs[:,1]>=1) & (mp_covs[:,0]!=mp_covs[:,1])
print(np.sum(cond))
#sub_sess_mps[cond]


# In[133]:


#annots
a = sub_sess_annots[:, 3]
total, bite, sip = len(annot_covs), np.sum(a==1), np.sum(a==2)
bite_missed = np.sum((a==1) & (annot_covs[:, 0]<0))
sip_missed = np.sum((a==2) & (annot_covs[:, 0]<0))
print("GT Annots total, bite, sip: ", total, bite, sip)
print("Annots missed bite, sip   : ", bite_missed, sip_missed)


# In[134]:


print(np.sum(annot_covs>=0, axis=0))


# In[165]:


print(np.sum(x_annots<=-0, axis=0))


# In[180]:


neg_cond = mp_covs[:, 0]==0
pos_cond = mp_covs[:, 0]>0

x_neg = x_var_mps[neg_cond, 0]
x_pos = x_var_mps[pos_cond, 0]
bins = np.linspace(-1, 0, 21)
plt.hist(x_neg, bins, alpha=0.5, label='Negative', color='red', edgecolor='black')
plt.hist(x_pos, bins, alpha=0.5, label='Positive', color='green', edgecolor='black')
plt.xlim([-1.0,0])
plt.ylabel('Count')
plt.xlabel('X of unit gravity ')
plt.legend(loc='upper left')
plt.show()

x_neg = x_vars[neg_cond, 2]
x_pos = x_vars[pos_cond, 2]
bins = np.linspace(0, 1, 21)

plt.hist(x_neg, bins, alpha=0.5, label='Negative', color='red', edgecolor='black')
plt.ylabel('Count')
plt.xlabel('Variance in acclearation')
plt.legend(loc='upper right')
plt.show()

plt.hist(x_pos, bins, alpha=0.5, label='Positive', color='green', edgecolor='black')
plt.ylabel('Count')
plt.xlabel('Variance in acclearation')
plt.legend(loc='upper right')
plt.show()



# In[189]:


#missing rate for difernt xth
total_annots = len(x_annots)
res = []
for xth in np.arange(0, -1.01, -0.05):
    xa = (x_annots<xth).astype(int)    
    #print(np.sum(xa, axis=0))
    xa = np.amax(xa, axis=1)
    fn = total_annots - np.sum(xa)
    fnr = 100*fn/total
    res.append([xth, fnr])
    print([xth, fn, fnr])
    
res = np.array(res)
plt.plot(res[:, 0], res[:,1])
plt.grid(True)
plt.show()
        


# In[200]:


#missing rate for difernt var-th at some xth

neg_cond = mp_covs[:, 0]==0
pos_cond = mp_covs[:, 0]>0

x_neg = x_var_mps[neg_cond, 0]
x_pos = x_var_mps[pos_cond, 0]
bins = np.linspace(-1, 0, 21)
plt.hist(x_neg, bins, alpha=0.5, label='Negative', color='red', edgecolor='black')
plt.hist(x_pos, bins, alpha=0.5, label='Positive', color='green', edgecolor='black')
plt.xlim([-1.0,0])
plt.ylabel('Count')
plt.xlabel('X of unit gravity ')
plt.legend(loc='upper left')
plt.show()


fig = plt.figure(figsize=(10,6))
ax = plt.subplot(111)

total_annots = len(x_annots)
for xth in np.arange(0, -.51, -0.1):
    xth = int(xth*100)/100
    res = []
    for varth in np.arange(0, 2.05, 0.1):
        xa = (x_annots>xth)
        va = np.copy(var_annots)
        va[xa] = -1
        va = (va>=varth).astype(int)        
        va = np.amax(va, axis=1)
        
        fn = total_annots - np.sum(va)
        fnr = 100*fn/total
        res.append([varth, fnr])
        print([xth, varth, fn, fnr])
    
    
    res = np.array(res)
    ax.plot(res[:, 0], res[:,1], label='XTh='+str(xth))
    
ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.1),
          ncol=3, fancybox=True, shadow=True)
    
plt.grid(True)
plt.show()
        


# In[205]:


#negative count var-th at some xth

neg_cond = mp_covs[:, 0]==0
x_neg = x_var_mps[neg_cond, 0]
var_neg = x_var_mps[neg_cond, 1]

fig = plt.figure(figsize=(10, 6))
ax = plt.subplot(111)

for xth in np.arange(0, -.51, -0.1):
    xth = int(xth*100)/100
    res = []
    for varth in np.arange(0, 2.05, 0.1):
        cond = (x_neg<=xth) & (var_neg>=varth)        
        res.append([varth, np.sum(cond)])
        print([xth, varth, fn, fnr])
    
    
    res = np.array(res)
    ax.plot(res[:, 0], res[:,1], label='XTh='+str(xth))
    
ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.25),
          ncol=3, fancybox=True, shadow=True)
    
plt.grid(True)
plt.show()
        


# In[ ]:







































































