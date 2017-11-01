data = [];
for i= 1:13
    filepath = strcat('K:\ASM\data\PublicData\eating_gatech\eating_detection_inertial_ubicomp2015\participants\', num2str(i),'\datafiles\waccel_tc_ss_label_bite.csv')
    data(i).data = csvread(filepath)
end

save('data', 'data')