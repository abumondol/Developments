
# coding: utf-8

# In[36]:


import numpy as np
import scipy as sp
import os 
import sys
import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


# In[37]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[38]:


lab_free= 'lab'
assert lab_free in ['lab', 'free']
ds = mfileu.read_file('data', '{}_data_steven_right_smoothed.pkl'.format(lab_free))
ssil = mfileu.read_file('peaks', '{}_ssil_steven_right.pkl'.format(lab_free))

if lab_free=='lab':
    annots = mfileu.read_file('data', '{}_annots_steven_right.pkl'.format(lab_free))
else:
    annots = mfileu.read_file('data', '{}_annots_steven_processed.pkl'.format(lab_free))


# In[39]:


subj, sess = 0, 0
a = annots[subj][sess]

si, ei = 13000, 14000
#si, ei = 832*16, 835*16
d = ds[subj][sess][si:ei, :2]
print(d)


if lab_free=='lab':
    print(a[:20, :])
    cond = (a[:, 2]==1) & (a[:,0]>=si) & (a[:,0]<=ei)
    a = a[cond, 0]
    print(a)
    print(a/16)
else:
    print(a)

cond = (ssil[:,0]==subj) & (ssil[:,1]==sess)
il = ssil[cond, 2:]
print(il.shape)
cond = (il[:,0]>=si) & (il[:,0]<=ei)
il = il[cond, :]



fig = plt.figure(figsize=(16, 10))
ax = plt.subplot(111)  

ax.plot(d[:,0], d[:,1])
for i in range(len(il)):
    c = ['r', 'b', 'g', 'k']
    c = c[il[i,1]]
    plt.axvline(x=il[i, 0]/16, color=c, linestyle='-')
    
if lab_free=='lab':
    ax.scatter(a/16, np.zeros((len(a), )), marker='x')
else:
    for i in range(len(a)):
        ax.plot([a[i,0], a[i,1]], [0, 0], linewidth=5)


plt.xlim([si/16, ei/16])
plt.ylim([-15, 15])
plt.grid(True)
plt.show()  

