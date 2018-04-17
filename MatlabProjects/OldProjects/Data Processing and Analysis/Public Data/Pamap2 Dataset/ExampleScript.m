% ExampleScript.m

%% Start of script
addpath('quaternion_library');      % include quaternion library
close all;                          % close all figures
clear;                              % clear all variables
clc;                                % clear the command terminal

%% Import and plot sensor data
load('pamap2_data.mat');

%% Process sensor data through algorithm

sub_count = length(pamap2_data)
for sub = 1:sub_count
    sess_count = length(pamap2_data(sub).session);
    for sess = 1:sess_count
        pos_count = length(pamap2_data(sub).session(sess).position);
        for pos = 1:pos_count
            fprintf('sub:%d, sess:%d, pos:%d\n', sub, sess, pos);
            Accelerometer = pamap2_data(sub).session(sess).position(pos).accel;
            Gyroscope = pamap2_data(sub).session(sess).position(pos).gyro;
            Magnetometer = pamap2_data(sub).session(sess).position(pos).mag;
            count = length(Accelerometer);
            
            quaternion = zeros(count, 4);
            start = true;            
            for i = 1:count
                if sum(isnan(Accelerometer(i, :))) > 0 || sum(isnan(Gyroscope(i, :))) > 0 || sum(isnan(Magnetometer(i, :))) > 0
                    start = true;
                    continue;
                end
                
                if start == true
                    AHRS = MadgwickAHRS('SamplePeriod', 1/100, 'Beta', 0.1);
                end
                
                AHRS.Update(Gyroscope(i,:), Accelerometer(i,:), Magnetometer(i,:));	% gyroscope units must be radians
                quaternion(i, :) = AHRS.Quaternion;
                start = false;
            end
            
            pamap2_data(sub).session(sess).position(pos).quat = quaternion;
            
        end
    end
end

save('pamap2_data', 'pamap2_data');



%% End of script