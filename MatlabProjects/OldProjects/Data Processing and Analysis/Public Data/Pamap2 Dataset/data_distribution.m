data = pamap2_data;
sub_count = length(data)
accel = [];
gyro = [];
for sub = 1:sub_count
    sess_count = length(data(sub).session);
    for sess = 1:1
        pos_count = length(data(sub).session(sess).position);
        for pos = 1:1
            fprintf('sub:%d, sess:%d, pos:%d\n', sub, sess, pos);
            a = data(sub).session(sess).position(pos).accel;
            g = data(sub).session(sess).position(pos).gyro;
            
            accel = [accel; a];
            gyro = [gyro; g];
                        
        end
    end
end

accel = accel(2:end, :) - accel(1:end-1, :);
a = min(accel)
b = max(accel)
c = min(gyro)
d = max(gyro)

ma = sqrt(sum(accel.*accel, 2));
mg = sqrt(sum(gyro.*gyro, 2));

d = [ma, mg(2:end)];

d = d(d(:,1) <10, :);

figure
hist(d(:,1), 1000);

figure
hist(d(:,2), 1000);
