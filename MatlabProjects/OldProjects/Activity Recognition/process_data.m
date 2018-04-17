function process_data()
load('data');

len = length(data);
fprintf('Processing data. Total data: %d\n', len);
for i=1:len
    d = data(i).raw_data;
    data(i).acc = d(d(:,2)== 1, 4:6);
    data(i).lacc = d(d(:,2)== 10, 4:6);
    data(i).gyro = d(d(:,2)== 4, 4:6);
    data(i).grav = d(d(:,2)== 9, 4:6);
    data(i).quat = d(d(:,2)== 11, 4:8);

end %end of function

save('data','data');
fprintf('Processing data done.\n');


end
