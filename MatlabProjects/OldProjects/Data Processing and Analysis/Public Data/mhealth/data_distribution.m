sub_count = length(mhealth_data)
accel = [];
gyro = [];
for sub = 1:sub_count
    sess_count = length(mhealth_data(sub).session);
    for sess = 1:sess_count
        pos_count = length(mhealth_data(sub).session(sess).position);
        for pos = 1:1
            fprintf('sub:%d, sess:%d, pos:%d\n', sub, sess, pos);
            a = mhealth_data(sub).session(sess).position(pos).accel;
            g = mhealth_data(sub).session(sess).position(pos).gyro;
            
            accel = [accel; a];
            gyro = [gyro; g];
                        
        end
    end
end

accel = accel(2:end, :) - accel(1:end-1, :);
ma = sqrt(sum(accel.*accel, 2));
mg = sqrt(sum(gyro.*gyro, 2));

a = min(accel)
b = max(accel)
c = min(gyro)
d = max(gyro)

figure
hist(ma, 100);

figure
hist(mg, 100);
