offset = [1339, 130, 1365];
for s =1:3
    accel = test_data(s).accel;
    ofs = offset(s)*1000;
    filename=strcat('test_data/sensor-', num2str(s+1), '-0-0-0-', num2str(ofs) );    
    dlmwrite(filename, accel, 'precision',20);
    
    %acn = data(s).accel_norm;
    %filename=strcat(dest_folder, 'acn_', num2str(s-1));
    %csvwrite(filename, acn);       
    %dlmwrite(filename, acn, 'precision',10);
end