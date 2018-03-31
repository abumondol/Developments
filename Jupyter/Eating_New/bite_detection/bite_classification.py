
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import sys
import os
from keras.utils import to_categorical
from sklearn.model_selection import train_test_split

from keras import backend as K
from keras.layers import Conv2D, MaxPooling2D, Dense, Flatten, Dropout
from keras.models import Sequential, model_from_json
from keras.optimizers import Adam
from keras.losses import categorical_crossentropy
from keras.callbacks import TensorBoard


# In[2]:


root_path ='C:/ASM/DevData/eating/eating_detection/'
x_th = -0.3
var_th = 0.25
min_bite_interval = 2*16
window_size = 6*16


# In[3]:


def create_directory(path):
    if not os.path.exists(path):
        print('Creating directory: ', path)
        os.makedirs(path)


# In[ ]:


with open('C:/ASM/DevData/eating/data/steven_uva_lab_data_combined.pkl', 'rb') as file:
    lab_data = pickle.load(file)

with open('C:/ASM/DevData/eating/data/steven_free_data.pkl', 'rb') as file:
    free_data = pickle.load(file)

