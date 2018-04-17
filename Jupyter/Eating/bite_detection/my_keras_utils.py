
# coding: utf-8

# In[1]:


import numpy as np

from keras.layers import Conv2D, MaxPooling2D, Dense, Flatten
from keras.models import Sequential, model_from_json
from keras.optimizers import Adam
from keras.losses import binary_crossentropy


# In[3]:


def train_model(X_train, Y_train, X_test, Y_test, batch_size, epochs):
    s = X_train.shape
    X_train = X_train.reshape((s[0], s[1], s[2], 1))
    s = X_test.shape
    X_test = X_test.reshape((s[0], s[1], s[2], 1))
    
    input_shape = (X_train.shape[1], X_train.shape[2], 1)

    model = Sequential()
    model.add(Conv2D(32, kernel_size=(2, 2), padding ='valid', strides=(1, 1),
                     activation='relu',
                     input_shape=input_shape ))
    model.add(MaxPooling2D(pool_size=(4, 1), strides=(4, 1)))

    model.add(Conv2D(64, kernel_size=(2, 2), padding ='valid', strides=(1, 1), activation='relu'))
    model.add(MaxPooling2D(pool_size=(4, 1), strides=(4, 1)))


    model.add(Flatten())
    model.add(Dense(100, activation='relu'))
    model.add(Dense(100, activation='relu'))
    model.add(Dense(1, activation='sigmoid'))

    model.compile(loss=binary_crossentropy,
                  optimizer= Adam(),
                  metrics=['accuracy'])

    model.fit(X_train, Y_train,
              validation_data = (X_test, Y_test),
              batch_size=batch_size,
              epochs=epochs)
    
    ypr = model.predict(X_test, verbose=0)

    return ypr


# In[ ]:


def test_model(model, X_test, Y_test):
    Ypr = model.predict(X_test, verbose=0)
    Yprb = Ypr[:, 0]>0.5
    Yprb = Yprb.reshape((len(Y_test), 1))
    Y = np.concatenate((Y_test, Yprb, Ypr), axis=1)
    return Y

