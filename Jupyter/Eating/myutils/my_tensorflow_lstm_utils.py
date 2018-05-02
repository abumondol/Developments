
# coding: utf-8

# In[1]:


import numpy as np
import tensorflow as tf


# In[2]:


def multi_layer_biLSTM(x, batch_size, n_hidden, n_layer):
    output = x
    for i in range(n_layer):
        cell_fw = tf.nn.rnn_cell.LSTMCell(n_hidden, state_is_tuple=True)
        cell_bw = tf.nn.rnn_cell.LSTMCell(n_hidden, state_is_tuple=True)

        initial_state_fw = cell_fw.zero_state(batch_size, tf.float32)
        initial_state_bw = cell_bw.zero_state(batch_size, tf.float32)

        (output_fw, output_bw), (last_state_fw, last_state_bw) = tf.nn.bidirectional_dynamic_rnn(cell_fw, cell_bw, output,
                                                                                 initial_state_fw=initial_state_fw,
                                                                                 initial_state_bw=initial_state_bw,
                                                                                 scope='BLSTM_layer_'+str(i),
                                                                                 dtype=tf.float32)


        output = tf.concat([output_fw, output_bw], axis=2)
    
    return output_fw, output_bw


# In[3]:


def multi_layer_LSTM(x, batch_size, n_hidden, n_layer, name_scope):    
    output = x
    for i in range(n_layer):
        cell_fw = tf.nn.rnn_cell.LSTMCell(n_hidden, state_is_tuple=True)        
        initial_state = cell_fw.zero_state(batch_size, tf.float32)


        output, last_state = tf.nn.dynamic_rnn(cell_fw, output,
                                               initial_state=initial_state,                                               
                                               scope=name_scope+'_LSTM_layer_'+str(i),
                                               dtype=tf.float32)
    
    return output

