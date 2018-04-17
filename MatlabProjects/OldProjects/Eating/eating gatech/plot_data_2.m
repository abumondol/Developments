acl = raw_data(3).acl(:, 2:end);
d = mysmooth(acl, 0.9);

count = length(acl)
step_size = 5000
for i = 1:step_size:count
    close all
    figure 
    plot(d(i:i+step_size,1));
     hold on
    grid on
    plot(d(i:i+step_size,2));
    plot(d(i:i+step_size,3));
   
    %plot(acl(i:i+step_size, 1));
    legend('S','NS')   
    
    waitforbuttonpress
    
end
