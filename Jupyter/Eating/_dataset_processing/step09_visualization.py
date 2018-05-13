
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[5]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_data_process_utils as mdpu
import my_segmentation_utils as msegu
import my_steven_lab_utils as mslabu
importlib.reload(mslabu)


# In[3]:


ds = mfileu.read_file('data', 'lab_data_steven.pkl')


# In[6]:


def magnitude(d):
    mag = np.sum(d*d, axis=1, keepdims=True)
    return mag
    #return np.concatenate((d, mag), axis=1)


# In[18]:


subj, sess = 0, 1
d = ds[subj][sess]["data_right"]
a = ds[subj][sess]["annots"]
a1 = mslabu.adjust_annots(a, subj, sess)

#a = a[a[:, 0]>=9508*16, :]
#a1 = a1[a1[:, 0]>=9508*16, :]

#a = a[a[:, 0]>=11716*16, :]
#a1 = a1[a1[:, 0]>=11716*16, :]

print(d.shape, a.shape)
#print(a)
#print(a1)


# In[19]:


mps = msegu.find_min_points_by_xth(d[:,-3], xth = - 0.3, min_bite_interval=2*16)
print(mps.shape)


# In[ ]:


def get_title(a, i, steven_lab=True):
    if steven_lab:
        s = "Time: "+str(a[i, 0]/16)+' - '+str([i, 1])+
        s+= "Bite/Sip: "+str(a[i, 2])
        s+= "Right/Sip: "+str(a[i, 3])
        s+= "Repeat: "+str(a[i, 4])
    else:
        s = "Time: "+str(a[i, 0]/16)+' - '+str([i, 1])+
        s+= "Bite/Sip: "+str(a[i, 2])
        s+= "Right/Sip: "+str(a[i, 3])
        s+= "Repeat: "+str(a[i, 4])


# In[20]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')
for i in range(0, 100):    
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
    ax.scatter(d[ms, 0], d[ms, -3], marker='D')
    ax.scatter(d[actual, 0], d[actual, -3], marker='x')
    ax.scatter(d[adjusted, 0], d[adjusted, -3], marker='o')
    
    #ax.plot(d[si:ei, 0], d[si:ei,4], linestyle=':', color='blue')
    #ax.plot(d[si:ei, 0], d[si:ei,5], linestyle=':', color='green')
    #ax.plot(d[si:ei, 0], d[si:ei,6], linestyle=':', color='red')
    
    plt.title(get_title(a, i))
    plt.ylim([-1,1])
    plt.grid(True)
    plt.show()
    
    #fig = plt.figure(figsize=(10,6))
    #ax = plt.subplot(111)    
    #ax.plot(d[si:ei, 0], d[si:ei,4:7])
    #plt.title('Accel '+str(a[i, 1]))    
    #plt.grid(True)
    #plt.show()
    
    #fig = plt.figure(figsize=(10,6))
    #ax = plt.subplot(111)    
    #ax.plot(d[si:ei, 0], magnitude(d[si:ei,7:]))
    #plt.title('Gyro '+str(a[i, 1]))
    #plt.ylim([0, 50])
    #plt.grid(True)
    #plt.show()

