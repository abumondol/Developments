function quaternion_to_grav()
load('data');
len = length(data);
fprintf('Quaternion to gravity. Total data: %d\n', len);

mov_avg_len = 10;
for i = 1:len
    q0 = data(i).quat(:,4);
    q1 = data(i).quat(:,1);
    q2 = data(i).quat(:,2);
    q3 = data(i).quat(:,3);
        
    gx = 2*(q1.*q3-q0.*q2);
    gy = 2*(q2.*q3+q0.*q1);
    gz = q0.*q0 - q1.*q1 - q2.*q2 + q3.*q3;
    %data(i).grav_quat = round([gx gy gz], 2);
    d = [gx gy gz];
    d = tsmovavg(d, 'e', mov_avg_len, 1);
    data(i).grav_quat = d(mov_avg_len:end,:);    
    
end
save('data','data');

fprintf('Quaternion to gravity done.\n');
end




