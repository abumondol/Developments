%load('data')
offset = 5;

for sub = 8:12    
    d = data(sub).data;
    a = data(sub).annots;
    duration = d(end,1)-d(1,1)
    rate = length(d)/duration
    
    annot_count = size(a, 1);
    for i = 1:annot_count
        t = a(i,1);        
        label = a(i,2);
        if i == 5
            break
        end
        
        close all;
        %figure('units','normalized','outerposition',[0 0 1 1]);
        %figure

        d1 = d(d(:,1) >= t-offset & d(:,1) <= t+offset, :);            
        %subplot(2,1,1)       % add first plot in 2 x 1 grid

        ttl = strcat('sub:', num2str(sub), ', annot no:', num2str(i), ', time:', num2str(t), ', label:', num2str(label) );            
        x = d1(:,1); % timestamps
        x = x - t;
        y = d1(:,2:4);        
        plot(x, y);
        legend('X','Y','Z');
        title(ttl);
        hold on
        grid on
        plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
        scatter(0, 0, 'rd'); % annotated mark point
        %scatter(a(i,1)-t, 0, 'bo'); % annotated mark point
        
%         subplot(2,1,2)       % add second plot in 2 x 1 grid            
%         y = d1(:,8:10);        
%         plot(x, y);
%         legend('X','Y','Z');                        
%         hold on
%         grid on
%         plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
%         scatter(0, 0, 'rd'); % annotated mark point          
%         scatter(a(i,1)-t, 0, 'bo'); % annotated mark point          

        waitforbuttonpress
        %waitforbuttonpress
    end
                
end


