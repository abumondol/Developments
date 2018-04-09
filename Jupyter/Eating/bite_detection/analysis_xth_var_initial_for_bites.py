
# coding: utf-8

# In[2]:


import numpy as np
import pickle
import os
import importlib
import matplotlib.pyplot as plt
import my_bite_detection_utils as bdu


# In[3]:


#importlib.reload(mtu)
importlib.reload(bdu)


# In[4]:


with open('C:/ASM/DevData/eating/data/steven_uva_lab_data_combined.pkl', 'rb') as file:
    ds = pickle.load(file)


# In[5]:


x_th = 0
min_bite_interval = 2*16
window_size = 6*16
max_annot_distance = 3*16

sub_sess_mps, sub_sess_annots,  = np.empty((0, 3), dtype=int), np.empty((0, 4), dtype=int)
mp_covs, annot_covs = np.empty((0, 5), dtype=int), np.empty((0, 5), dtype=int)
x_var_mps = np.empty((0, 2))
x_annots = np.empty((0, 3))
var_annots = np.empty((0, 3))
grav_accel_gyro_vals = np.empty((0, window_size, 9))

total_duration = 0
window_duration = 0

for subject in range(len(ds)):
    for sess in range(len(ds[subject])):
        print("Subejct, sess: ", subject, sess)
        data = ds[subject][sess]['data']
        annots = ds[subject][sess]['annots']
        
        mps = bdu.find_min_points_by_xth(data[:, 1], x_th, min_bite_interval)
        mps = bdu.remove_min_points_at_boundary(mps, len(data), window_size//2)
        total_duration += data[-1,0]
        window_duration += len(mps)*window_size/16
        
        
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
        
        grav_accel_gyro_vals = np.concatenate((grav_accel_gyro_vals, w), axis=0)
        
        
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


# In[6]:


print(len(sub_sess_mps))
print(total_duration, window_duration, len(sub_sess_mps)*6, 100*window_duration/total_duration)


# In[7]:


#Windows
total, neg, bite, sip = len(mp_covs), np.sum(mp_covs[:,0]==0), np.sum(mp_covs[:,0]==1), np.sum(mp_covs[:,0]==2)
print("Windows total, neg, bite, sip: ", total, neg, bite, sip)


# In[6]:


print(np.sum(mp_covs>=1, axis=0))


# In[7]:


#same window covered by different types of annots (both bite and sip)
cond = (mp_covs[:,0]>=1) & (mp_covs[:,1]>=1) & (mp_covs[:,0]!=mp_covs[:,1])
print(np.sum(cond))
#sub_sess_mps[cond]


# In[8]:


#annots
a = sub_sess_annots[:, 3]
total, bite, sip = len(annot_covs), np.sum(a==1), np.sum(a==2)
bite_missed = np.sum((a==1) & (annot_covs[:, 0]<0))
sip_missed = np.sum((a==2) & (annot_covs[:, 0]<0))
print("GT Annots total, bite, sip: ", total, bite, sip)
print("Annots missed bite, sip   : ", bite_missed, sip_missed)


# In[9]:


print(np.sum(annot_covs>=0, axis=0))


# In[10]:


print(np.sum(x_annots<=-0, axis=0))


# In[19]:


neg_cond = mp_covs[:, 0]==0
pos_cond = mp_covs[:, 0]>0

x_neg = x_var_mps[neg_cond, 0]
x_pos = x_var_mps[pos_cond, 0]
bins = np.linspace(-1, 0, 21)
plt.hist(x_neg, bins, alpha=0.5, label='Negative Winodws', color='red', edgecolor='black')
plt.hist(x_pos, bins, alpha=0.5, label='Positive Windows', color='green', edgecolor='black')
plt.xlim([-1.0,0])
plt.ylabel('Count', fontsize=14)
plt.xlabel('MPX Threshold', fontsize=14)
plt.legend(loc='upper left')
#plt.grid(True)
plt.show()

res = []
for xth in np.arange(-1, 0.01, 0.05):
    #xth = int(xth*100)/100
    count = np.sum(x_neg<=xth)
    print(xth, count)
    res.append([xth, count])
res = np.array(res)
print(res.shape)
plt.plot(res[:, 0], res[:, 1], color='red')
plt.xlim([-1,0])
plt.ylabel('Count of Negative Windows', fontsize=14)
plt.xlabel('MPX Threshold', fontsize=14)
plt.grid(True)
plt.show()

res = []
for xth in np.arange(-1, 0.01, 0.05):
    #xth = int(xth*100)/100
    count = np.sum(x_pos<=xth)
    print(xth, count)
    res.append([xth, count])
res = np.array(res)
print(res.shape)
plt.plot(res[:, 0], res[:, 1], color='green')
plt.xlim([-1,0])
plt.ylabel('Count of Positive Windows', fontsize=14)
plt.xlabel('MPX Threshold', fontsize=14)
plt.grid(True)
plt.show()
    
    


# In[18]:


var_neg = x_var_mps[neg_cond, 1]   
var_pos = x_var_mps[pos_cond, 1]
bins = np.linspace(0, 2, 21)

plt.hist(var_neg, bins, alpha=0.5, color='red', edgecolor='black')
plt.xlim([0, 1])
plt.ylabel('Count of Negative Windows', fontsize=14)
plt.xlabel('Variance in acclearation', fontsize=14)
#plt.grid(True)
plt.show()

plt.hist(var_pos, bins, alpha=0.5, color='green', edgecolor='black')
plt.xlim([0, 1])
plt.ylabel('Count of Positive Windows', fontsize=14)
plt.xlabel('Variance in acclearation', fontsize=14)
#plt.grid(True)
plt.show()


max_var = 5
res = []
for varth in np.arange(0, max_var, 0.1):
    varth = int(varth*100)/100
    count = np.sum(var_neg>=varth)
    print(varth, count)
    res.append([varth, count])
res = np.array(res)
print(res.shape)
plt.plot(res[:, 0], res[:, 1], color='red')
plt.xlim([0, max_var])
plt.ylabel('Count of Negative Windows', fontsize=14)
plt.xlabel('Threshold of variance in acceleration', fontsize=14)
plt.grid(True)
plt.show()

res = []
for varth in np.arange(0, max_var, 0.1):
    varth = int(varth*100)/100
    count = np.sum(var_pos>=varth)
    print(varth, count)
    res.append([varth, count])
res = np.array(res)
print(res.shape)
plt.plot(res[:, 0], res[:, 1], color='green')
plt.xlim([0, max_var])
plt.ylabel('Count of Positive Windows', fontsize=14)
plt.xlabel('Threshold of variance in acceleration', fontsize=14)
plt.grid(True)
plt.show()


# In[13]:


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
plt.ylabel('False negative rate (%)', fontsize=14)
plt.xlabel('MPX Threhold', fontsize=14)
plt.grid(True)
plt.show()
        


# In[14]:


#missing rate for difernt var-th at some xth
fig = plt.figure(figsize=(10,6))
ax = plt.subplot(111)

total_annots = len(x_annots)
for xth in np.arange(0, -.51, -0.1):
    xth = int(xth*100)/100
    res = []
    for varth in np.arange(0, 2.05, 0.1):
        varth = int(varth*100)/100
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
    ax.plot(res[:, 0], res[:,1], label='MPX='+str(xth))
    
ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.1),
          ncol=3, fancybox=True, shadow=True)

plt.ylabel('False negative rate (%)', fontsize=16)
plt.xlabel('Minimum variance in acceleration', fontsize=16)
plt.grid(True)
plt.show()
        


# In[15]:


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
        varth = int(varth*100)/100
        cond = (x_neg<=xth) & (var_neg>=varth)
        count = np.sum(cond)
        res.append([varth, count])
        print([xth, varth, count])
    
    
    res = np.array(res)
    ax.plot(res[:, 0], res[:,1], label='MPX='+str(xth))
    
ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.1),
          ncol=3, fancybox=True, shadow=True)

plt.ylabel('Count of Negative Windows', fontsize=16)
plt.xlabel('Minimum variance in acceleration', fontsize=16)
plt.grid(True)
plt.show()
        


# In[16]:


#positive count var-th at some xth
pos_cond = mp_covs[:, 0]>0
x_pos = x_var_mps[pos_cond, 0]
var_pos = x_var_mps[pos_cond, 1]

fig = plt.figure(figsize=(10, 6))
ax = plt.subplot(111)

for xth in np.arange(0, -.51, -0.1):
    xth = int(xth*100)/100
    res = []
    for varth in np.arange(0, 2.05, 0.1):
        varth = int(varth*100)/100
        cond = (x_pos<=xth) & (var_pos>=varth)
        count = np.sum(cond)
        res.append([varth, count])
        print([xth, varth, count])
    
    
    res = np.array(res)
    ax.plot(res[:, 0], res[:,1], label='MPX='+str(xth))
    
ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.1),
          ncol=3, fancybox=True, shadow=True)

plt.ylabel('Count of Positive Windows', fontsize=16)
plt.xlabel('Minimum variance in acceleration', fontsize=16)
plt.grid(True)
plt.show()


# In[17]:


gag = grav_accel_gyro_vals
grav = gag[:, :, 0:3]
accel = gag[:, :, 3:6].flatten()
gyro = gag[:, :, 6:].flatten()

#weights = np.ones_like(accel)/float(len(accel))
#plt.hist(accel, bins=100, weights=weights)
#plt.show()

#plt.hist(gyro, bins=100, weights=weights)
#plt.show()

print(len(accel))
print(np.sum((accel>19.6) | (accel<-19.6)))

print(len(gyro))
print(np.sum((accel>15) | (accel<-15)))




# In[ ]:







































































