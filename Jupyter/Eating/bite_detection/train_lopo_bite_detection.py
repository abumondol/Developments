
# coding: utf-8

# In[57]:


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
import my_keras_utils as mku


# In[64]:


importlib.reload(mtu)
importlib.reload(bdu)
importlib.reload(mcu)
importlib.reload(mku)


# In[61]:


root_path ='C:/ASM/DevData/eating' if "C:" in os.getcwd() else "."
print("Root path: ", root_path)

params = {"x_th": -0, "min_bite_interval": 3*16, "window_size": 6*16}
windows, ssml, features = bdu.get_windows_lab(params)
labels = ssml[:,-1]
print("Sindow, ssml, label shape: ", windows.shape, ssml.shape, labels.shape)
print("Neg, Pos, Bite, Sip, X count: ", np.sum(labels==0), np.sum(labels>0), np.sum(labels==1), np.sum(labels==2), np.sum(labels==-1))

labels[labels==2] = 1
cond = labels>=0
windows = windows[cond]
ssml = ssml[cond]
labels = labels[cond].reshape((-1,1))
features = features[cond]
print("Neg, Pos, Bite, Sip, X count: ", np.sum(labels==0), np.sum(labels>0), np.sum(labels==1), np.sum(labels==2), np.sum(labels==-1))


# In[62]:


#Training Parameters
net_params={}
net_params['learning_rate'] = 0.001
net_params['num_epochs'] = 50
net_params['batch_size'] = 128
net_params['keep_prob_val'] = 0.5


# In[66]:


params["x_th"] = -.3
for var_th in np.arange(0, 2.1, .25):    
    var_th = int(var_th*100)/100    
    if var_th !=0.5: 
        continue
    
    params["var_th"] = var_th 
    cond = (features[:, 0] <=params["x_th"]) & (features[:, -1] >=params["var_th"]) & (ssml[:, 0]>=7)
    w = windows[cond]
    s = ssml[cond]
    l = labels[cond]
    f = features[cond]
    print(w.shape, s.shape, np.sum(l, axis=0))
    
    for exclude_subject in range(7, 28):
        print("******** Exclude Subject {} *********".format(exclude_subject))
        print(params)
        cond = s[:,0]!=exclude_subject
        train_x = w[cond]
        train_y = l[cond]
        
        cond = s[:,0]==exclude_subject
        test_x = w[cond]        
        test_y = l[cond]

        print("Train Test shapes: ", train_x.shape, test_x.shape)
        print("Train Test labels:", np.sum(train_y, axis=0), np.sum(test_y, axis=0))
        
        path = root_path+'/outputs/bite_detection_lopo/'+bdu.param_string(params)+"/subject_"+str(exclude_subject)     
        #test_pred, train_result, test_result = mtu.train_test_model(train_x, train_y, test_x, test_y, folder_path=path, params=net_params)
        ypr = mku.train_model(train_x, train_y, test_x, test_y, batch_size=128, epochs = 100)
        res = mcu.get_metrics(ypr, test_y)
        
        #path = path+'/results'
        #bdu.create_directory(path)
        #np.savetxt(path+'/test_y.csv', test_y, fmt='%.4f', delimiter=',')    
        #np.savetxt(path+'/test_prediction.csv', test_pred, fmt='%.4f', delimiter=',')
        #np.savetxt(path+'/train_result.csv', train_result, fmt='%.4f', delimiter=',')
        #np.savetxt(path+'/test_result.csv', test_result, fmt='%.4f', delimiter=',') 


# In[ ]:


test_result = np.array(test_result)
print(test_result)

