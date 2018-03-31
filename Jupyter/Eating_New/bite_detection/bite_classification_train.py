
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import sys
import bite_detection_utils as bdu

from sklearn.model_selection import train_test_split
from keras.utils import to_categorical
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


# In[4]:


with open('C:/ASM/DevData/eating/data/steven_uva_lab_data_combined.pkl', 'rb') as file:
    lab_data = pickle.load(file)


# In[7]:


windows, windows_left, windows_right, ssml = bdu.get_windows_labels_for_dataset(
    lab_data, 
    x_th = x_th, 
    var_th=var_th,
    min_bite_interval = min_bite_interval,
    window_size = window_size)

print(windows.shape, windows_left.shape, windows_right.shape, ssml.shape)


# In[ ]:


def train_model(X_train, Y_train, X_val, Y_val, batch_size, epochs, tensorboard_logdir):
    print("Starting training... Sizes: ", X_train.shape, Y_train.shape, X_val.shape, Y_val.shape)    
    create_directory(tensorboard_logdir)        
    
    input_shape = (X_train.shape[1], X_train.shape[2], X_train.shape[3])

    model = Sequential()
    model.add(Conv2D(32, kernel_size=(4, 1), padding ='same', strides=(1, 1), activation='relu', input_shape=input_shape ))
    model.add(MaxPooling2D(pool_size=(4, 1), strides=(2, 1)))    

    model.add(Conv2D(64, kernel_size=(2, 2), padding ='same', strides=(1, 1), activation='relu'))
    model.add(MaxPooling2D(pool_size=(4, 1), strides=(2, 1)))    
    
    model.add(Conv2D(128, kernel_size=(2, 2), padding ='same', strides=(1, 1), activation='relu'))
    model.add(MaxPooling2D(pool_size=(4, 1), strides=(2, 1)))    

    model.add(Flatten())
    model.add(Dense(128, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Dense(64, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Dense(32, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Dense(3, activation='softmax'))
    
    model.compile(loss = categorical_crossentropy, optimizer= Adam())
    
    if len(X_val)==0:
        model.fit(X_train, Y_train,
                  batch_size=batch_size,
                  epochs=epochs,
                  shuffle=True,
                  validation_data=(X_val, Y_val),
                  callbacks=[TensorBoard(log_dir=tensorboard_logdir)])
    else:
        model.fit(X_train, Y_train,
                  batch_size=batch_size,
                  epochs=epochs,
                  shuffle=True,                  
                  callbacks=[TensorBoard(log_dir=tensorboard_logdir)])             

    return model


# In[ ]:


epochs = 5
subject_list = list(range(-1, len(lab_data)))
w = windows
w = w.resahpe((w.shape[0], w.shape[1], w.shape[2], 1))


for exclude_subject in subject_list:
    print('\n\n**************************************')
    print('Excluding subject:', exclude_subject)
    print('**************************************\n')
        
    cond = ssml[:,0]!=exclude_subject
    w = windows[cond, :, :, :]
    l = labels[cond, :]
    X = np.concatenate((X, w)) 
    Y = np.concatenate((Y, l))
    
    
    path = root_path+'results/window_'+str(window_size)+'_free_'+str(int(include_free_data))+'_weighted_'+str(int(weighted))+'/'
    tensorboard_logdir = path +'tensorboard/subject_'+str(exclude_subject)+'/'
    
    X_train, X_val, Y_train, Y_val = train_test_split(X, Y, stratify=Y, test_size=0.1)
    model = train_model(X_train, Y_train, 
                              X_val, Y_val,
                              batch_size = 128,
                              epochs=epochs,
                              tensorboard_logdir=tensorboard_logdir,
                              class_weights=class_weights)    
    
    model_path = path+'models/'
    create_directory(model_path)    
    
    model.save(model_path+'subject_'+str(exclude_subject)+'.h5')    

