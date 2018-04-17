load fisheriris
return

d = realdisp_quat(2).session(2).right_Rz;
d = d(500:520, :);
d = sum(d.*d, 2)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
d = realdisp_data(1).session(1).right_lower_arm;
q = d(100:200, 10:13);
sum(q.*q, 2);

alphaby2_1 = acos(q(:,1));

a = q(:,2:4);
b = sqrt(sum(a.*a, 2));
alphaby2_2 = asin(b);
res = [alphaby2_1-alphaby2_2]

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
