
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[16]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import Meal_Window_Generation_Utils as mwgenu
import my_data_process_utils as mdpu
import my_feature_utils as mfeatu
importlib.reload(mwgenu)


# In[3]:


hand = 'right'
ds = mfileu.read_file('data', 'free_data_steven_'+hand+'.pkl')
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[4]:


win_size, step_size = 10*16, 8
params={'win_size':win_size, 'step_size':step_size}


# In[17]:


ixs = mwgenu.get_train_window_indices_all(ds, annots, win_size=win_size, step_size=step_size, blockPrint=False)


# In[18]:


print(ixs.shape)
for i in range(0, 4):
    print(i, np.sum(ixs[:, -1]==i))

print()
ixs2 = ixs[ixs[:, -2]>=0, :]
print(ixs2.shape)
for i in range(0, 4):
    print(i, np.sum(ixs2[:, -1]==i))


# In[20]:


ixf = ixs[(ixs[:, -1]<=1), :]
print("All Shapes window, labels: ", ixf.shape, np.sum(ixf[:,-1]==0), np.sum(ixf[:,-1]==1), np.sum(ixf[:,-1]==2), np.sum(ixf[:,-1]==3))


# In[ ]:


#windows = mwgenu.get_window_data(ds, ixf, win_size=win_size)
#print(windows.shape)


# In[21]:


v = mfeatu.get_variance_accel(ds, ixf, win_size=win_size)
gx = mfeatu.get_grav_x(ds, ixf, index_offset=win_size//2)


# In[ ]:



for vth in np.arange(0,2, 0.5):
    vv = v[ixf[:, -1]==0]


# In[42]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

vth = 1

vn = v[ixf[:, -1]==0]
vp = v[ixf[:, -1]>0]
print(vn.shape, vp.shape)

gxn =gx[ixf[:, -1]==0] 
gxp = gx[ixf[:, -1]>0]
vn = vn[(gxn<=-0)]
vp = vp[(gxp<=-0)]
print(vn.shape, vp.shape)

vn = vn[(vn>=vth) & (vn<=30)]
vp = vp[(vp>=vth) & (vp<=30)]
print(vn.shape, vp.shape)

plt.hist(vn, bins=50)
plt.xlim([0, 50])
plt.show()
plt.hist(vp, bins=50)
plt.xlim([0, 50])
plt.show()

