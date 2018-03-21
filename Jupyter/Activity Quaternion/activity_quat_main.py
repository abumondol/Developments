
# coding: utf-8

# In[2]:

import numpy as np
import pickle
import sklearn.metrics as metrics
import argparse
import myutils

from keras.layers import Conv2D, MaxPooling2D, LSTM, Dense, Flatten, Permute, Reshape
from keras.models import Sequential, model_from_json
from keras.optimizers import Adam
from keras.losses import binary_crossentropy, categorical_crossentropy


# In[3]:

method = 0
direction = 0
augment = 0
print('Method: {}, Direction: {}, Augment: {}'.format(method, direction, augment))


# In[4]:

#path = 'C:/ASM/DevData/activity_quaternion/' 
path='' 
with open(path+'data/processed.pkl', 'rb') as file:
    data = pickle.load(file)


# In[5]:

def get_train_test_windows(train_data, test_data, train_label, test_label, win_len, step_size, augment_vals=[]):    
    train_data_norm, test_data_norm = myutils.znorm(train_data[:, :9], test_data[:, :9]) 
    
    if method == 0 and direction == 0:
        tr_data = np.concatenate((train_data_norm[:, :6], train_data[:, -3:]), axis = 1)
        ts_data = np.concatenate((test_data_norm[:, :6], test_data[:, -3:]), axis = 1)
    elif method ==0 and direction==1:
        tr_data = np.concatenate((train_data_norm[:, :6], train_data[:, -9:]), axis = 1)
        ts_data = np.concatenate((test_data_norm[:, :6], test_data[:, -9:]), axis = 1)
    elif method==1 and direction==0:
        tr_data, ts_data = train_data_norm[:, :6], test_data_norm[:, :6]
    else:
        tr_data, ts_data = train_data_norm, test_data_norm
        
    trainX, trainY = myutils.get_windows(tr_data, train_label, win_len, step_size, augment_vals)
    testX, testY = myutils.get_windows(ts_data, test_label, win_len, step_size)
    
    print('Data Shapes:', trainX.shape, trainY.shape, testX.shape, testY.shape)
    return trainX, trainY, testX, testY
    


# In[6]:

def train_model(X_train, Y_train, batch_size, epochs):    
    NUM_FILTERS = 64    
    FILTER_SHAPE = (5, 1)
    NUM_UNITS_LSTM = 128
    NUM_CLASSES = Y_train.shape[1]
    
    input_shape = (X_train.shape[1], X_train.shape[2], X_train.shape[3])
    print(input_shape)
    

    model = Sequential()
    model.add(Conv2D(NUM_FILTERS, kernel_size=FILTER_SHAPE, padding ='valid', strides=(1, 1), activation='relu',
                     input_shape=input_shape))
    #model.add(MaxPooling2D(pool_size=(4, 1), strides=(4, 1)))
    model.add(Conv2D(NUM_FILTERS, kernel_size=FILTER_SHAPE, padding ='valid', strides=(1, 1), activation='relu'))
    model.add(Conv2D(NUM_FILTERS, kernel_size=FILTER_SHAPE, padding ='valid', strides=(1, 1), activation='relu'))
    model.add(Conv2D(NUM_FILTERS, kernel_size=FILTER_SHAPE, padding ='valid', strides=(1, 1), activation='relu'))
        
    model.add(Permute((2, 1, 3)))    
    
    #model.add(Flatten())    
    #model.add(Dense(100, activation='relu'))
    #model.add(Dense(100, activation='relu'))
    #model.add(Dense(1, activation='sigmoid'))
    
    model.add(Reshape((-1, NUM_FILTERS*input_shape[1])))
    model.add(LSTM(NUM_UNITS_LSTM, return_sequences=True))
    model.add(LSTM(NUM_UNITS_LSTM))
    model.add(Dense(NUM_CLASSES, activation='softmax'))

    print(model.summary())
    model.compile(loss = categorical_crossentropy,
                  optimizer= 'rmsprop',
                  metrics=['accuracy'])

    model.fit(X_train, Y_train,
              batch_size=batch_size,            
              epochs=epochs, verbose=1)

    return model
    


# In[7]:

def test_model(model, X_test, Y_test):
    count = Y_test.shape[0]
    
    Y_test = np.argmax(Y_test, axis=1).reshape((count,1))    
    Y_prob = model.predict(X_test, verbose=0)
    Y_pred = np.argmax(Y_prob, axis=1).reshape((count,1))    
    Y = np.concatenate((Y_test, Y_pred, Y_prob), axis=1)
    return Y


# In[8]:

step_size = 15
batch_size = 64
epochs = 100
model_str=str(method)+str(direction)+str(augment)

for win_len in range(30, 31, 30):
    augment_vals = []
    if augment==1:    
        augment_vals = [.8, .9, 1.1, 1.2]

    print('\n##################################################')
    print('Window len: {}, Method: {}, Direction: {}, Augment: {}, Augment_Vals:{}'.format(win_len, method, direction, augment, augment_vals))
    print('##################################################\n')
    
    train_data, train_label, test_data, test_label = myutils.get_train_test_data(data)
    print(np.max(train_label), np.max(test_label))    
    print('data shapes: ', train_data.shape, train_label.shape, test_data.shape, test_label.shape)    
    
    
    X_train, Y_train, X_test, Y_test = get_train_test_windows(train_data, test_data, train_label, test_label,
                                                              win_len, step_size, augment_vals)

    
    model = train_model(X_train, Y_train, batch_size, epochs)
    model.save(path + "models/model_{}_{}.h5".format(model_str, win_len))

    Y = test_model(model, X_test, Y_test)
    model.save(path + "results/result_{}_{}.csv".format(model_str, win_len))

    test_true = Y[:, 0]
    test_pred = Y[:, 1]
    print("\tTest fscore(weighted):\t{} ".format(metrics.f1_score(test_true, test_pred, average='weighted')))
    print("\tTest fscore:\t{} ".format(metrics.f1_score(test_true, test_pred, average=None)))


# In[ ]:



