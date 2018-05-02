
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_data_process_utils as mdpu
import my_segmentation_utils as msegu
import my_steven_lab_utils as mslabu
importlib.reload(mslabu)
importlib.reload(mdpu)
importlib.reload(msegu)


# In[3]:


ds = mfileu.read_file('data', 'lab_data_uva.pkl')


# In[5]:


subj, sess = 15, 0
d = np.copy(ds[subj][sess]["data_right"])
d[:, 4:10] = mdpu.smooth_data(d[:, 4:10], 0.9)
diff = d[1:, :] - d[:-1, :]
diff = np.concatenate((diff[0, :].reshape((1, -1)), diff))
diff[:, 0] = d[:, 0]

#spd = mdpu.get_spatio_temporal_series(d[:, -3:], radius=0.25)


annots = ds[subj][sess]["annots"]
#annots1 = mslabu.adjust_annots(annots, subj, sess)


print(d.shape, diff.shape, annots.shape)
#print(spd.shape, np.min(spd[:, 1]-spd[:,0]+1), np.max(spd[:, 1]-spd[:,0]+1), np.mean(spd[:, 1]-spd[:,0]+1), np.median(spd[:, 1]-spd[:,0]+1))


# In[6]:


mps = msegu.find_min_points_by_xth(d[:,-3], xth = - 0.4, min_bite_interval=2*16)
print(mps.shape)


# In[7]:


def get_title(a, i, steven_lab=True):
    #print(a[i,1])
    if steven_lab:        
        s = "Time: {}-{}, Bite/Sip: {}, Right/Left: {}, Repeat:{}"
        s = s.format(a[i,0]/16, a[i, 1]/16, a[i,2], a[i,3], a[i,4])        
    else:
        s = "Time: {}, Food Type: {}, Bite/Sip: {}"
        s = s.format(a[i,0]/16, a[i, 1], a[i,2])        
        
    return s

def get_features(d, diff, window_size, step_size):
    res = []    
    for i in range(window_size//2, len(d)-window_size//2, step_size):
        t = d[i, 0]
        d1 = diff[i-window_size//2:i+window_size//2, -3:]
        diff1 = diff[i-window_size//2:i+window_size//2, -3:]
        
        grav_diff = np.sum(np.absolute())/len(d)
        grav_x_diff = np.mean(diff[i-window_size//2:i+window_size//2, -3:])
        
        res.append([t, grav_x_diff, grav_diff])
        
    return np.array(res)


    


# In[9]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

a = annots[annots[:, 2]==2, :]
a1 = annots1[annots1[:, 2]==2, :]
print(a.shape)
for i in range(len(a)):    
    si = a[i, 0] - 100
    ei = a[i, 0] + 100
   
    ms = mps[(mps>=si) & (mps<=ei)]
   
    fig = plt.figure(figsize=(12,5))
    ax = plt.subplot(111)    
    ax.plot(d[si:ei, 0], d[si:ei,-3], color='blue')
    ax.plot(d[si:ei, 0], d[si:ei,-2], color='green')
    ax.plot(d[si:ei, 0], d[si:ei,-1], color='red')
    #ax.plot(d[si:ei, 0], magnitude(d[si:ei,4:7]), color='black')
    
    actual, adjusted = a[i,0], a1[i, 0]
    #print(actual, adjusted)
    ax.scatter(d[ms, 0], d[ms, -3], marker='D', s=70)
    ax.scatter(d[actual, 0], d[actual, -3], marker='o', s=70)
    #ax.scatter(d[adjusted, 0], d[adjusted, -3], marker='s', s=100)
    
    '''
    ix = spd[(spd[:, 0]>=si) & (spd[:, 1]<=ei), 0]    
    print(ix.shape, si-ei+1)
    if len(ix)>0:        
        ax.scatter(d[ix, 0], d[ix, -3], marker='x', s=70, color='blue')
        ax.scatter(d[ix, 0], d[ix, -2], marker='x', s=70, color='green')
        ax.scatter(d[ix, 0], d[ix, -1], marker='x', s=70, color='red')
    
    '''
    
    plt.title(get_title(a, i, steven_lab=False))
    plt.ylim([-1,1])
    plt.grid(True)
    plt.show()
    
    
    fig = plt.figure(figsize=(12,5))
    ax = plt.subplot(111)
    
    ax.plot(d[si:ei, 0], d[si:ei,7], color='blue')
    ax.plot(d[si:ei, 0], d[si:ei,8], color='green')
    ax.plot(d[si:ei, 0], d[si:ei,9], color='red')
    
    #f = get_features(d[si:ei, :], diff[si:ei, :], 16, 4)    
    #ax.scatter(f[:, 0], f[:, 1], marker='o', s=40)
    #ax.scatter(f[:, 0], f[:, 2], marker='x', s=70)
        
    plt.ylim([-1, 1])
    plt.grid(True)
    plt.show()
    
    fig = plt.figure(figsize=(12,5))
    ax = plt.subplot(111)
    
    ax.plot(d[si:ei, 0], d[si:ei,4], color='blue')
    ax.plot(d[si:ei, 0], d[si:ei,5], color='green')
    ax.plot(d[si:ei, 0], d[si:ei,6], color='red')
    
    #f = get_features(d[si:ei, :], diff[si:ei, :], 16, 4)    
    #ax.scatter(f[:, 0], f[:, 1], marker='o', s=40)
    #ax.scatter(f[:, 0], f[:, 2], marker='x', s=70)
        
    plt.ylim([-2, 2])
    plt.grid(True)
    plt.show()
    
    
  

