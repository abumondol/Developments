
# coding: utf-8

# In[66]:


import numpy as np
import pickle
import sys
import my_bite_detection_utils as bdu
import my_classification_utils as mcu
import os

from sklearn.model_selection import train_test_split
from sklearn.utils import shuffle
import tensorflow as tf
import my_tensorflow_utils as mtu
import importlib
from sklearn import metrics


# In[67]:


importlib.reload(mtu)
importlib.reload(bdu)
importlib.reload(mcu)


# In[43]:


if "C:" in os.getcwd():
    root_path ='C:/ASM/DevData/eating'
else:
    root_path = "."
print("Root path: ", root_path)    

params = {"x_th": -0,          
          "min_bite_interval": 2*16,
          "window_size": 6*16}


# In[44]:


#Training Parameters
net_params={}
net_params['learning_rate'] = 0.001
net_params['num_epochs'] = 100
net_params['batch_size'] = 128
net_params['keep_prob_val'] = 0.5


# In[48]:


windows, ssml, labels, features = bdu.get_windows_lab(params)
print(windows.shape, ssml.shape, np.sum(labels, axis=0))

params["x_th"] = -.3
params["var_th"] = 0.5
cond = (features[:, 0] <=params["x_th"]) & (features[:, -1] >=params["var_th"])
windows = windows[cond]
ssml = ssml[cond]
labels = labels[cond]
features = features[cond]
print(windows.shape, ssml.shape, np.sum(labels, axis=0))


# In[49]:


x = np.arange(len(ssml))
y = ssml[:, -1]
train_indices, test_indices, _, _ = train_test_split(x, y, test_size=0.2, stratify=y)

train_x = windows[train_indices]
test_x  = windows[test_indices]
train_y = labels[train_indices, :]
test_y = labels[test_indices, :]

print("Total Train Test: ",np.sum(labels, axis=0), np.sum(train_y, axis=0), np.sum(test_y, axis=0))       

path = root_path+'/outputs/bite_detection_epoch_validation/'+bdu.param_string(params)     
test_pred, train_result, test_result = mtu.train_test_model(train_x, train_y, test_x, test_y, folder_path=path, params=net_params)


# In[52]:


path = path+'/results'
bdu.create_di
rectory(path)
np.savetxt(path+'/test_y.csv', test_y, fmt='%.4f', delimiter=',')    
np.savetxt(path+'/test_prediction.csv', test_pred, fmt='%.4f', delimiter=',')
np.savetxt(path+'/train_result.csv', train_result, fmt='%.4f', delimiter=',')
np.savetxt(path+'/test_result.csv', test_result, fmt='%.4f', delimiter=',') 


# In[72]:


train_result = np.array(train_result)
test_result = np.array(test_result)

#train_result_smooth = mcu.smooth(train_result, 0.8)
#test_result_smooth = mcu.smooth(test_result, 0.8)
#print(train_result_smooth.shape)

import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')
plt.plot(train_result[:, 0], train_result[:, 1], alpha=0.8, color='blue')
plt.plot(train_result[:, 0], test_result[:, 1], alpha=0.8, color='red')
#plt.plot(train_result[:, 0], train_result_smooth[:, 1], color='blue')

plt.ylabel('Loss', fontsize=14)
plt.xlabel('Epoch', fontsize=14)
plt.grid(True)
plt.show()

plt.plot(train_result[:, 0], train_result[:, -1], alpha=0.8, color='blue')
plt.plot(train_result[:, 0], test_result[:, -1], alpha=0.8, color='red')
#plt.plot(train_result[:, 0], train_result_smooth[:, 1], color='blue')

plt.ylabel('F1-score', fontsize=14)
plt.xlabel('Epoch', fontsize=14)
plt.grid(True)
plt.show()

