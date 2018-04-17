srcFolder = 'C:/Users/mm5gg/Box Sync/Data Sets/Eating m2fed/usc_free_living/staff_1/data';
file_names = {'W-2-14432D35F47689C-m2fed-01-03-Right-2017-06-07-14-13-42.wada',
     'W-3-14422D3CF36A67A-m2fed-01-02-Right-2017-06-07-13-53-33.wada',
     'W-4-14442D34F8B155A-m2fed-01-01-Right-2017-06-07-14-14-51.wada'};
 
 offset=[1339, 130, 1365];
 
 test_data = [];
for i =1:3    
    file_path = strcat(srcFolder,'/',file_names{i})
    data = csvread(file_path);
    
    if data(1,2) == 0
        data = data(2:end, :);
    end
    
    
    data = data(data(:,2)==1, [1, 4:6]);
    data(:,1) = round(data(:,1)/1e6, 0);
    data(:,1) = data(:,1)-data(1,1);
    %data(:,1) = data(:,1)/1e9 + offset(i);
    
    test_data(i).accel = data;
    test_data(i).offset = offset(i);
        
end

save('test_data', 'test_data');
