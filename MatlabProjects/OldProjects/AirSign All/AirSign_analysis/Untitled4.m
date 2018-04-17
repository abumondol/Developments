if axis_count == 3
            stability = 1./std(d);          
            stability = stability';        
            s = sum(stability);
            weight =  stability/s; 
        elseif axis_count == 6
            stability = 1./std(d);          
            stability = stability';
            stability_acc = stability(1:3);
            s = sum(stability_acc);
            weight_acc =  stability_acc/s;
            
            stability_gyro = stability(4:6);
            s = sum(stability_gyro);
            weight_gyro =  stability_gyro/s;
            
        else
            fprintf('XXXXXX ERROR XXXXXXXX Axis count %d \n', axis_count);
            return;
        end  