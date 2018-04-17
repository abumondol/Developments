d = samsung;
%size(d)

acl = d(:, 2:4);
grav = d(:, 8:10);
quat = d(:, 14:17);

q = quat(10,:)
qc = quaternion_conjugate(q)
qm = quaternion_multiplication(q, qc)

q1 = quat(100,:)
qm = quaternion_multiplication(q1, qc)

return

len = size(d, 1);
for i = 1:len
    q = quat(i, :);
    [Rx, Ry, Rz] = quaternion_to_rotation_matrix(q);        
    g = grav(i,:);
    g = g/norm(g)
    waitforbuttonpress
end
