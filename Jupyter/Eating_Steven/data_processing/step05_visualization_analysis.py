
# coding: utf-8

# In[2]:


import numpy as np
import pickle
import os
import matplotlib.pyplot as plt


# In[3]:


with open('C:/ASM/DevData/eating_steven/data/lab_data_steven.pkl', 'rb') as file:
    ds = pickle.load(file)


# In[24]:


annots = np.empty((0,7))
for subj in range(len(ds)):
    for sess in range(len(ds[subj])):
        print("Subj sess: ", subj, sess)
        a = ds[subj][sess]["annots"]
        subj_sess = np.zeros((len(a),2))
        subj_sess[:,0] = subj
        subj_sess[:,1] = sess
        a = np.concatenate((a, subj_sess), axis=1)
        annots = np.concatenate((annots, a))

a = annots
print(len(a))
for hand in range(4):
    b = np.sum((a[:,2]==1) & (a[:,3]==hand)) #& (a[:,4]==0)
    s = np.sum((a[:,2]==2) & (a[:,3]==hand))
    print(hand, b, s, b+s)


# In[3]:


def magnitude(d):
    mag = np.sum(d*d, axis=1, keepdims=True)
    return np.concatenate((d, mag), axis=1)


# In[4]:


def grav_change_mag(d, width):
    


# In[5]:


subj, sess = 4, 1
d = ds[subj][sess]["data"]
a = ds[subj][sess]["annots"]
print(d.shape, a.shape)


# In[15]:


for i in range(10, 30):    
    si = a[i, 0] - 160
    ei = a[i, 0] + 160
   
   
    fig = plt.figure(figsize=(15,5))
    ax = plt.subplot(111)    
    ax.plot(d[si:ei, 0], d[si:ei,1], color='blue')
    ax.plot(d[si:ei, 0], d[si:ei,2], color='green')
    ax.plot(d[si:ei, 0], d[si:ei,3], color='red')
    plt.title('Grav, Type: '+str(a[i, 1])+", Time: "+str(a[i,0]/16))
    plt.ylim([0,-20])
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

